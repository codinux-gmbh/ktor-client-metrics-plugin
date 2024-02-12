package net.codinux.web.ktor.client.metrics

import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.util.date.*
import net.codinux.web.ktor.client.metrics.MetricsPluginConfigBase.AppliedConfig

class MetricsService(
    private val config: AppliedConfig
) {

    private val contextAttributeKey: AttributeKey<Any> = AttributeKey("MetricsContext")

    suspend fun onSend(sender: Send.Sender, request: HttpRequestBuilder): HttpClientCall {
        val requestTime = GMTDate()

        return try {
            val context = config.meterRegistry.startingRequest(request)
            context?.let { request.attributes.put(contextAttributeKey, context) }

            val originalCall = sender.proceed(request)
            stopTimerWithSuccessStatus(config, originalCall.response)

            originalCall
        } catch (e: Throwable) {
            stopTimerWithErrorStatus(config, request, requestTime, e)

            throw e
        }
    }

    private fun stopTimerWithSuccessStatus(config: AppliedConfig, response: HttpResponse) {
        val request = response.request
        val responseData = ResponseData(request.method, request.url, response.status.value, request.attributes, response.requestTime, response.responseTime)

        stopTimer(config, responseData)
    }

    private fun stopTimerWithErrorStatus(config: AppliedConfig, request: HttpRequestBuilder, requestTime: GMTDate, cause: Throwable) {
        val errorCode = when (cause) {
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> 504

            else -> 500
        }

        val responseData = ResponseData(request.method, request.url.build(), errorCode, request.attributes, requestTime, GMTDate(), cause)

        stopTimer(config, responseData)
    }

    private fun stopTimer(config: AppliedConfig, responseData: ResponseData) {
        val url = responseData.url

        val standardTags = mutableMapOf(
            "host" to url.host + (if (url.port == url.protocol.defaultPort) "" else ":${url.port}"), // only append port if it's not protocol default port
            "uri" to (config.getUriTag?.invoke(url) ?: config.meterRegistry.getUriTag(url) ?: url.encodedPath),
            "method" to responseData.method.value,
            "status" to responseData.httpStatusCode.toString(),
            "outcome" to (config.meterRegistry.calculateOutcome(responseData.httpStatusCode) ?: "n/a"),
            "exception" to getExceptionClassName(responseData.exception)
        )

        config.configureTags?.invoke(standardTags, responseData)
        val tags = standardTags + config.additionalTags
        val context = responseData.attributes.getOrNull(contextAttributeKey)

        config.meterRegistry.responseRetrieved(context, responseData, tags)
    }

    private fun getExceptionClassName(throwable: Throwable?) = throwable?.let {
        val exceptionClass = throwable::class
        // TODO: do not use forClass.qualifiedName on JS, it will produce an error
        exceptionClass.simpleName.takeUnless { it.isNullOrBlank() } ?: exceptionClass.qualifiedName
    }
        ?: "none"

}
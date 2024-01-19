package net.codinux.web.ktor.client.metrics

import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*

class MetricsService(
    pluginConfig: MetricsPluginConfig
) {

    private val config = pluginConfig.applyConfig()

    private val contextAttributeKey: AttributeKey<Any> = AttributeKey("MetricsContext")

    suspend fun onSend(sender: Send.Sender, request: HttpRequestBuilder): HttpClientCall {
        return try {
            val context = config.meterRegistry.sendingRequest(request)
            context?.let { request.attributes.put(contextAttributeKey, context) }

            val originalCall = sender.proceed(request)
            stopTimerWithSuccessStatus(config, originalCall.response)

            originalCall
        } catch (e: Throwable) {
            stopTimerWithErrorStatus(config, request, e)

            throw e
        }
    }

    private fun stopTimerWithSuccessStatus(config: MetricsPluginConfig.AppliedConfig, response: HttpResponse) {
        val request = response.request
        stopTimer(config, request.url, request.method, response.status.value, request.attributes)
    }

    private fun stopTimerWithErrorStatus(config: MetricsPluginConfig.AppliedConfig, request: HttpRequestBuilder, cause: Throwable) {
        val errorCode = when (cause) {
            is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> 504

            else -> 500
        }

        stopTimer(config, request.url.build(), request.method, errorCode, request.attributes, cause)
    }

    private fun stopTimer(config: MetricsPluginConfig.AppliedConfig, url: Url, method: HttpMethod, status: Int, attributes: Attributes, throwable: Throwable? = null) {
        val parameters = mapOf(
            "host" to url.host + (if (url.port == url.protocol.defaultPort) "" else ":${url.port}"), // only append port if it's not protocol default port
            "uri" to (config.meterRegistry.getUriTag(url) ?: url.encodedPath),
            "method" to method.value,
            "status" to status.toString(),
            "outcome" to (config.meterRegistry.calculateOutcome(status) ?: "n/a"),
            "exception" to getExceptionClassName(throwable)
        )

        val tags = parameters
        val context = attributes.getOrNull(contextAttributeKey)

        config.meterRegistry.responseRetrieved(context, tags)
    }

    private fun getExceptionClassName(throwable: Throwable?) = throwable?.let {
        val exceptionClass = throwable::class
        // TODO: do not use forClass.qualifiedName on JS, it will produce an error
        exceptionClass.simpleName.takeUnless { it.isNullOrBlank() } ?: exceptionClass.qualifiedName
    }
        ?: "none"

}
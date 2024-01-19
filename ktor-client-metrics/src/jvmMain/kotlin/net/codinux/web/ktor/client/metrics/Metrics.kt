package net.codinux.web.ktor.client.metrics

import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import net.codinux.web.ktor.client.metrics.MetricsPluginConfig.AppliedConfig

class MetricsPluginConfig {
    lateinit var meterRegistry: MeterRegistry

    data class AppliedConfig(
        val meterRegistry: MeterRegistry,
        val additionalAttributes: Map<String, String>
    )

    internal fun applyConfig(): AppliedConfig {
        if (this::meterRegistry.isInitialized == false) {
            throw IllegalArgumentException("meterRegistry must be set")
        }

        return AppliedConfig(meterRegistry)
    }
}

val Metrics = createClientPlugin("Metrics", ::MetricsPluginConfig) {

    val config = pluginConfig.applyConfig()

    on(Send) { request ->
        try {
            val context = config.meterRegistry.sendingRequest(request)
            context?.let { request.attributes.put(contextAttributeKey, context) }

            val originalCall = proceed(request)
            stopTimerWithSuccessStatus(config, originalCall.response)

            originalCall
        } catch (e: Throwable) {
            stopTimerWithErrorStatus(config, request, e)

            throw e
        }
    }
}

private val contextAttributeKey: AttributeKey<Any> = AttributeKey("MetricsContext")

private fun stopTimerWithSuccessStatus(config: AppliedConfig, response: HttpResponse) {
    val request = response.request
    stopTimer(config, request.url, request.method, response.status.value, request.attributes)
}

private fun stopTimerWithErrorStatus(config: AppliedConfig, request: HttpRequestBuilder, cause: Throwable) {
    val errorCode = when (cause) {
        is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> 504

        else -> 500
    }

    stopTimer(config, request.url.build(), request.method, errorCode, request.attributes, cause)
}

private fun stopTimer(config: AppliedConfig, url: Url, method: HttpMethod, status: Int, attributes: Attributes, throwable: Throwable? = null) {
    val parameters = mapOf(
        "host" to url.host + (if (url.port == url.protocol.defaultPort) "" else ":${url.port}"), // only append port if it's not protocol default port
        "uri" to (config.meterRegistry.getUriTag(url) ?: url.encodedPath),
        "method" to method.value,
        "status" to status.toString(),
        "outcome" to (config.meterRegistry.calculateOutcome(status) ?: "n/a"),
        "exception" to getExceptionClassName(throwable)
    )

    // From Spring source code: Make sure that KeyValues entries are already sorted by name for better performance
    val tags = parameters.toSortedMap()
    val context = attributes.getOrNull(contextAttributeKey)

    config.meterRegistry.responseRetrieved(context, tags)
}

private fun getExceptionClassName(throwable: Throwable?) = throwable?.javaClass?.let { exceptionClass ->
    exceptionClass.simpleName.takeUnless { it.isNullOrBlank() } ?: exceptionClass.name
}
    ?: "none"

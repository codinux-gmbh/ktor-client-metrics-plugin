package net.codinux.web.ktor.client.metrics

import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.api.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.binder.http.Outcome
import java.net.URLDecoder

class MetricsPluginConfig {
    lateinit var meterRegistry: MeterRegistry
}

val Metrics = createClientPlugin("Metrics", ::MetricsPluginConfig) {
    on(Send) { request ->
        try {
            val sample = Timer.start(pluginConfig.meterRegistry)
            request.attributes.put(sampleAttributeKey, sample)

            val originalCall = proceed(request)
            stopTimerWithSuccessStatus(pluginConfig, originalCall.response)

            originalCall
        } catch (e: Throwable) {
            stopTimerWithErrorStatus(pluginConfig, request, e)

            throw e
        }
    }
}

private val sampleAttributeKey: AttributeKey<Timer.Sample> = AttributeKey("TimerSample")

private fun stopTimerWithSuccessStatus(config: MetricsPluginConfig, response: HttpResponse) {
    val request = response.request
    stopTimer(config, request.url, request.method, response.status.value, request.attributes)
}

private fun stopTimerWithErrorStatus(config: MetricsPluginConfig, request: HttpRequestBuilder, cause: Throwable) {
    val errorCode = when (cause) {
        is HttpRequestTimeoutException,
            is ConnectTimeoutException,
            is SocketTimeoutException -> 504

        else -> 500
    }

    stopTimer(config, request.url.build(), request.method, errorCode, request.attributes, cause)
}

private fun stopTimer(config: MetricsPluginConfig, url: Url, method: HttpMethod, status: Int, attributes: Attributes, throwable: Throwable? = null) {
    val parameters = mapOf(
        "host" to url.host,
        "uri" to URLDecoder.decode(url.encodedPath, Charsets.UTF_8),
        "method" to method.value,
        "status" to status.toString(),
        "outcome" to Outcome.forStatus(status).asKeyValue().value,
        "exception" to getExceptionClassName(throwable)
    )

    // From Spring source code: Make sure that KeyValues entries are already sorted by name for better performance
    val tags = parameters.toSortedMap().map { Tag.of(it.key, it.value) }
    val sample = attributes[sampleAttributeKey]

    sample.stop(config.meterRegistry.timer("http.client.requests", Tags.of(tags)))
}

private fun getExceptionClassName(throwable: Throwable?) = throwable?.javaClass?.let { exceptionClass ->
    exceptionClass.simpleName.takeUnless { it.isNullOrBlank() } ?: exceptionClass.name
}
    ?: "none"

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
import net.codinux.web.ktor.client.metrics.MetricsPluginConfig.AppliedConfig
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicInteger

class MetricsPluginConfig {
    lateinit var meterRegistry: MeterRegistry

    data class AppliedConfig(
        val meterRegistry: MeterRegistry
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
    val active = config.meterRegistry.gauge("http.client.requests" + ".active", AtomicInteger(0))!!

    on(Send) { request ->
        try {
            active.incrementAndGet()
            val sample = Timer.start(config.meterRegistry)
            request.attributes.put(sampleAttributeKey, sample)

            val originalCall = proceed(request)
            stopTimerWithSuccessStatus(config, originalCall.response)
            active.decrementAndGet()

            originalCall
        } catch (e: Throwable) {
            stopTimerWithErrorStatus(config, request, e)
            active.decrementAndGet()

            throw e
        }
    }
}

private val sampleAttributeKey: AttributeKey<Timer.Sample> = AttributeKey("TimerSample")

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

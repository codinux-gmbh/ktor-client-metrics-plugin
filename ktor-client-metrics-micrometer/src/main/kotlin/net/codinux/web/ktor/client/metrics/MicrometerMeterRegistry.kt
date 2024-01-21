package net.codinux.web.ktor.client.metrics

import io.ktor.client.request.*
import io.ktor.http.*
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.binder.http.Outcome
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicInteger

class MicrometerMeterRegistry(
    private val micrometerRegistry: io.micrometer.core.instrument.MeterRegistry,
    private val metricName: String = "http.client.requests"
) : MeterRegistry {

    init {
        if (metricName.isBlank()) {
            throw IllegalArgumentException("Metric name should be defined")
        }
    }

    private val active = micrometerRegistry.gauge(metricName + ".active", AtomicInteger(0))!!

    override fun startingRequest(request: HttpRequestBuilder): Timer.Sample {
        active.incrementAndGet()
        return Timer.start(micrometerRegistry)
    }

    override fun responseRetrieved(context: Any?, tags: Map<String, String>) {
        val sample = context as? Timer.Sample
        // From Spring source code: Make sure that KeyValues entries are already sorted by name for better performance
        val sortedTags = tags.toSortedMap().map { Tag.of(it.key, it.value) }

        sample?.stop(micrometerRegistry.timer(metricName, Tags.of(sortedTags)))

        active.decrementAndGet()
    }


    override fun getUriTag(url: Url) =
        URLDecoder.decode(url.encodedPath, Charsets.UTF_8)

    override fun calculateOutcome(httpStatusCode: Int) =
        Outcome.forStatus(httpStatusCode).asKeyValue().value

}
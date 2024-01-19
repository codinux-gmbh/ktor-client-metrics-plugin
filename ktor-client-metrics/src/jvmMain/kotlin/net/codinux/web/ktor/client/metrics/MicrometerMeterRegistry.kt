package net.codinux.web.ktor.client.metrics

import io.ktor.client.request.*
import io.ktor.http.*
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import io.micrometer.core.instrument.binder.http.Outcome
import java.net.URLDecoder
import java.util.concurrent.atomic.AtomicInteger

open class MicrometerMeterRegistry(
    protected open val micrometerRegistry: io.micrometer.core.instrument.MeterRegistry
) : MeterRegistry {

    protected open val active = micrometerRegistry.gauge("http.client.requests" + ".active", AtomicInteger(0))!!

    override fun sendingRequest(request: HttpRequestBuilder): Timer.Sample {
        active.incrementAndGet()
        return Timer.start(micrometerRegistry)
    }

    override fun responseRetrieved(context: Any?, tags: Map<String, String>) {
        val sample = context as? Timer.Sample
        sample?.stop(micrometerRegistry.timer("http.client.requests", Tags.of(tags.map { Tag.of(it.key, it.value) })))

        active.decrementAndGet()
    }


    override fun getUriTag(url: Url) =
        URLDecoder.decode(url.encodedPath, Charsets.UTF_8)

    override fun calculateOutcome(httpStatusCode: Int) =
        Outcome.forStatus(httpStatusCode).asKeyValue().value

}
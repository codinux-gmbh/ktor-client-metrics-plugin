package net.codinux.web.micrometer.apachehttpclient5

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ObservationExecChainHandler
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler
import io.micrometer.observation.ObservationRegistry
import net.codinux.web.micrometer.common.JacksonObjectMapper
import net.codinux.web.micrometer.model.Joke
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.io.entity.EntityUtils

class ApacheHttpClient5JokeRepository(
    meterRegistry: MeterRegistry
) {

    private val observationRegistry = ObservationRegistry.create().apply {
        this.observationConfig()
            .observationHandler(DefaultMeterObservationHandler(meterRegistry))
    }

    private val client = HttpClientBuilder.create()
        .addExecInterceptorLast("micrometer", ObservationExecChainHandler(observationRegistry))
        .build()

    private val request = HttpGet("https://v2.jokeapi.dev/joke/Programming?blacklistFlags=racist,sexist")

    fun getJoke(): Joke? =
        client.execute(request) { response ->
            EntityUtils.toString(response.entity)?.let {
                JacksonObjectMapper.deserializeJoke(it)
            }
        }

}
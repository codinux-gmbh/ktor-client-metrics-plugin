package net.codinux.web.micrometer.javahttpclient

import io.micrometer.core.instrument.binder.jdk.MicrometerHttpClient
import io.micrometer.core.instrument.MeterRegistry
import net.codinux.web.micrometer.common.Constants
import net.codinux.web.micrometer.common.JacksonObjectMapper
import net.codinux.web.micrometer.common.JokeRepository
import net.codinux.web.micrometer.model.Joke
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class JavaHttpClientJokeRepository(
    meterRegistry: MeterRegistry
) : JokeRepository {

    private val client = MicrometerHttpClient.instrumentationBuilder(HttpClient.newHttpClient(), meterRegistry).build()

    private val request = HttpRequest.newBuilder(URI(Constants.JokeUrl))
        .GET()
        .build()

    override fun getJoke(): Joke? =
        client.send(request, HttpResponse.BodyHandlers.ofString()).body()?.let {
            JacksonObjectMapper.deserializeJoke(it)
        }
}
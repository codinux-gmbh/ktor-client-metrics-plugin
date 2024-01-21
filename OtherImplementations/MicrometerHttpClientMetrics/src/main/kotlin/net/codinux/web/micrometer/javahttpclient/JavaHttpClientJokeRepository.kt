package net.codinux.web.micrometer.javahttpclient

import io.micrometer.core.instrument.binder.jdk.MicrometerHttpClient
import io.micrometer.core.instrument.MeterRegistry
import net.codinux.web.micrometer.common.JacksonObjectMapper
import net.codinux.web.micrometer.model.Joke
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class JavaHttpClientJokeRepository(
    meterRegistry: MeterRegistry
) {

    private val client = MicrometerHttpClient.instrumentationBuilder(HttpClient.newHttpClient(), meterRegistry).build()

    private val request = HttpRequest.newBuilder(URI("https://v2.jokeapi.dev/joke/Programming?blacklistFlags=racist,sexist"))
        .GET()
        .build()

    fun getJoke(): Joke? =
        client.send(request, HttpResponse.BodyHandlers.ofString()).body()?.let {
            JacksonObjectMapper.deserializeJoke(it)
        }
}
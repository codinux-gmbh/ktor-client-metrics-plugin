package net.codinux.web.micrometer.okhttp

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import net.codinux.web.micrometer.model.Joke
import okhttp3.OkHttpClient
import okhttp3.Request

class OkHttpJokeRepository(
    meterRegistry: MeterRegistry
) {

    private val client = OkHttpClient.Builder()
        .eventListener(OkHttpMetricsEventListener.builder(meterRegistry, "OkHttpClient").build())
        .build()

    private val request = Request.Builder()
        .url("https://v2.jokeapi.dev/joke/Programming?blacklistFlags=racist,sexist")
        .build()

    private val objectMapper = ObjectMapper()
        .registerModules(KotlinModule.Builder().build())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    fun getJoke(): Joke? = client.newCall(request).execute().let { response ->
        val joke = response.body?.string()?.let { objectMapper.readValue(it, Joke::class.java) }
        response.close()
        joke
    }

}
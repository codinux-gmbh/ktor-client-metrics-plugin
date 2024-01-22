package net.codinux.web.micrometer.okhttp

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import net.codinux.web.micrometer.common.Constants
import net.codinux.web.micrometer.common.JacksonObjectMapper
import net.codinux.web.micrometer.common.JokeRepository
import net.codinux.web.micrometer.model.Joke
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.use

class OkHttpJokeRepository(
    meterRegistry: MeterRegistry
) : JokeRepository {

    private val client = OkHttpClient.Builder()
        .eventListener(OkHttpMetricsEventListener.builder(meterRegistry, "OkHttpClient").build())
        .build()

    private val request = Request.Builder()
        .url(Constants.JokeApiUrl)
        .build()

    override fun getJoke(): Joke? = client.newCall(request).execute().let { response ->
        response.use {
            response.body?.string()?.let {
                JacksonObjectMapper.deserializeJoke(it)
            }
        }
    }

    override fun callNotExistingUrl() {
        client.newCall(Request.Builder().url(Constants.NotExistingUrl).build()).execute()
    }

}
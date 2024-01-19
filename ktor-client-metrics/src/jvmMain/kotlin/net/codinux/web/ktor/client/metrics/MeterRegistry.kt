package net.codinux.web.ktor.client.metrics

import io.ktor.client.request.*
import io.ktor.http.*

interface MeterRegistry { // cannot make interface generic as otherwise create config object with ::MetricsPluginConfig wouldn't work anymore

    fun sendingRequest(request: HttpRequestBuilder): Any?

    fun responseRetrieved(context: Any?, tags: Map<String, String>)

    fun getUriTag(url: Url): String? = null

    fun calculateOutcome(httpStatusCode: Int): String? = null

}
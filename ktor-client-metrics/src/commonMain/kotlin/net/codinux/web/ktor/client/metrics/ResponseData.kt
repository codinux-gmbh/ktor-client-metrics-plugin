package net.codinux.web.ktor.client.metrics

import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*

data class ResponseData(
    val method: HttpMethod,
    val url: Url,
    val httpStatusCode: Int,
    val attributes: Attributes,
    val requestTime: GMTDate,
    val responseTime: GMTDate,
    val exception: Throwable? = null
)

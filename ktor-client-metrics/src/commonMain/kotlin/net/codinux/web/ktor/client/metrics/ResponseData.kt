package net.codinux.web.ktor.client.metrics

import io.ktor.http.*
import io.ktor.util.*

data class ResponseData(
    val method: HttpMethod,
    val url: Url,
    val httpStatusCode: Int,
    val attributes: Attributes,
    val exception: Throwable? = null
)

package net.codinux.web.ktor.client.metrics

import io.ktor.http.*

abstract class MetricsPluginConfigBase {

    /**
     * Add additional tags to metrics
     */
    var additionalTags: Map<String, String> = emptyMap()

    /**
     * Adjust the value of the "uri" tag.
     * Return `null` if the tag value for this Url should be set to the default value [Url.encodedPath].
     */
    var getUriTag: ((Url) -> String?)? = null

    var configureTags: ((standardTags: MutableMap<String, String>, response: ResponseData) -> Unit)? = null

    data class AppliedConfig(
        val meterRegistry: MeterRegistry,
        val additionalTags: Map<String, String>,
        val getUriTag: ((Url) -> String?)?,
        val configureTags: ((standardTags: MutableMap<String, String>, response: ResponseData) -> Unit)?
    )

}
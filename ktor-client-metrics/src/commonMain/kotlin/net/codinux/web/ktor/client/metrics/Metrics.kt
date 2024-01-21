package net.codinux.web.ktor.client.metrics

import io.ktor.client.plugins.api.*
import io.ktor.http.*
import io.ktor.util.*

class MetricsPluginConfig {
    lateinit var meterRegistry: MeterRegistry

    /**
     * Add additional tags to metrics
     */
    var additionalTags: Map<String, String> = emptyMap()

    /**
     * Adjust the value of the "uri" tag.
     * Return `null` if the tag value for this Url should be set to the default value [Url.encodedPath].
     */
    var getUriTag: ((Url) -> String?)? = null

    var configureTags: ((standardTags: MutableMap<String, String>, HttpMethod, Url, status: Int, Attributes, throwable: Throwable?) -> Unit)? = null

    data class AppliedConfig(
        val meterRegistry: MeterRegistry,
        val additionalTags: Map<String, String>,
        val getUriTag: ((Url) -> String?)?,
        val configureTags: ((standardTags: MutableMap<String, String>, HttpMethod, Url, status: Int, Attributes, throwable: Throwable?) -> Unit)?
    )

    internal fun applyConfig(): AppliedConfig {
        if (this::meterRegistry.isInitialized == false) {
            throw IllegalArgumentException("meterRegistry must be set")
        }

        return AppliedConfig(meterRegistry, additionalTags, getUriTag, configureTags)
    }
}

val Metrics = createClientPlugin("Metrics", ::MetricsPluginConfig) {

    val metricsService = MetricsService(pluginConfig)

    on(Send) { request ->
        metricsService.onSend(this, request)
    }
}

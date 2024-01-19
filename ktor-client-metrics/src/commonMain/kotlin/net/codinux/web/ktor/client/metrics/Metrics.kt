package net.codinux.web.ktor.client.metrics

import io.ktor.client.plugins.api.*

class MetricsPluginConfig {
    lateinit var meterRegistry: MeterRegistry

    data class AppliedConfig(
        val meterRegistry: MeterRegistry,
        val additionalAttributes: Map<String, String>
    )

    internal fun applyConfig(): AppliedConfig {
        if (this::meterRegistry.isInitialized == false) {
            throw IllegalArgumentException("meterRegistry must be set")
        }

        return AppliedConfig(meterRegistry)
    }
}

val Metrics = createClientPlugin("Metrics", ::MetricsPluginConfig) {

    val metricsService = MetricsService(pluginConfig)

    on(Send) { request ->
        metricsService.onSend(this, request)
    }
}

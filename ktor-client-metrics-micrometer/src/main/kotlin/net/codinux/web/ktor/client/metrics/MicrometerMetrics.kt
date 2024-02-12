package net.codinux.web.ktor.client.metrics

import io.ktor.client.plugins.api.*


val MicrometerMetrics = createClientPlugin("MicrometerMetrics", ::MicrometerMetricsPluginConfig) {

    val metricsService = MetricsService(pluginConfig.applyConfig())

    on(Send) { request ->
        metricsService.onSend(this, request)
    }

}
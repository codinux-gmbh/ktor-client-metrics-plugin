package net.codinux.web.ktor.client.metrics

import io.ktor.client.plugins.api.*


val Metrics = createClientPlugin("Metrics", ::MetricsPluginConfig) {

    val metricsService = MetricsService(pluginConfig.applyConfig())

    on(Send) { request ->
        metricsService.onSend(this, request)
    }
}

package net.codinux.web.ktor.client.metrics

class MetricsPluginConfig : MetricsPluginConfigBase() {

    lateinit var meterRegistry: MeterRegistry

    internal fun applyConfig(): AppliedConfig {
        if (this::meterRegistry.isInitialized == false) {
            throw IllegalArgumentException("meterRegistry must be set")
        }

        return AppliedConfig(meterRegistry, additionalTags, getUriTag, configureTags)
    }
}
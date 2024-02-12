package net.codinux.web.ktor.client.metrics

import io.micrometer.core.instrument.MeterRegistry

class MicrometerMetricsPluginConfig : MetricsPluginConfigBase() {

    lateinit var meterRegistry: MeterRegistry

    internal fun applyConfig(): AppliedConfig {
        if (this::meterRegistry.isInitialized == false) {
            throw IllegalArgumentException("meterRegistry must be set")
        }

        return AppliedConfig(MicrometerMeterRegistry(meterRegistry), additionalTags, getUriTag, configureTags)
    }

}
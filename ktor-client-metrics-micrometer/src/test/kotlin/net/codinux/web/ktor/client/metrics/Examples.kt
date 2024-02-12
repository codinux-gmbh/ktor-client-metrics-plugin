package net.codinux.web.ktor.client.metrics

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.net.URLDecoder
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class Examples {

    // set your Micrometer MeterRegistry here, e.g. via dependency injection in Spring or Quarkus
    private val micrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    // configure your engine here
    private val engine = MockEngine {
        respondOk("Ok")
    }

    @Test
    fun showConfiguration() {
        val client = HttpClient(engine) {
            install(MicrometerMetrics) {
                this.meterRegistry = micrometerRegistry

                // specify additional tags that should get added to all metrics
                this.additionalTags = mapOf(
                    "client" to "Android-App"
                )

                // by default url's path is used. If it doesn't meet your needs, adjust uri tag value:
                this.getUriTag = { url ->
                    URLDecoder.decode(url.encodedPathAndQuery, Charsets.UTF_8) // URLDecoder is only available on the JVM
                }

                // the default tags - host, uri, method, status, outcome and exception - are passed via standardTags.
                // You can freely adjust them, e.g. via response values passed by response parameter: httpMethod, url, status, attributes and exception (if any)
                this.configureTags = { standardTags, response ->
                    standardTags.remove("exception")
                    standardTags["error-message"] = response.exception?.message ?: "none"
                }
            }
        }
    }
}
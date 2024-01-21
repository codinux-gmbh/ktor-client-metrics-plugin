package net.codinux.web.ktor.client.metrics

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class Examples {

    // configure your engine here
    private val engine = MockEngine {
        respondOk("Ok")
    }

    // create your MeterRegistry implementation
    private val registry = object : MeterRegistry {

        override fun startingRequest(request: HttpRequestBuilder): Any? {
            // you can return a context object, that is then passed to responseRetrieved() method as first parameter,
            // e.g. for Micrometer we start and return a Timer.Sample object here
            return null
        }

        override fun responseRetrieved(context: Any?, response: ResponseData, tags: Map<String, String>) {
            val durationMillis = response.responseTime.timestamp - response.requestTime.timestamp
            val status = response.httpStatusCode
            val url = response.url.toString()

            // handle metrics, e.g. send to a backend
        }

    }

    @Test
    fun simpleExample() {
        val client = HttpClient(engine) {
            install(Metrics) {
                this.meterRegistry = registry
            }
        }
    }

    @Test
    fun showConfiguration() {
        val client = HttpClient(engine) {
            install(Metrics) {
                this.meterRegistry = registry

                // specify additional tags that should get added to all metrics
                this.additionalTags = mapOf(
                    "client" to "Android-App"
                )

                // by default url's path is used. If it doesn't meet your needs, adjust uri tag value:
                this.getUriTag = { url ->
                    url.encodedPathAndQuery
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
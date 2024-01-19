package net.codinux.web.ktor.client.metrics

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MetricsPluginTest {

    @Test
    fun `Successful call GET to url adds 200 SUCCESS metric`() = runTest {
        val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val engine = MockEngine {
            respondOk("Ok")
        }

        val underTest = HttpClient(engine) {
            install(Metrics) {
                this.meterRegistry = prometheusRegistry
            }
        }

        underTest.get("https://example.com/user") { }

        val metrics = prometheusRegistry.scrape()

        assertThat(metrics).all {
            contains("""# HELP http_client_requests_seconds  
# TYPE http_client_requests_seconds summary""")
            contains("""http_client_requests_seconds_count{exception="none",host="example.com",method="GET",outcome="SUCCESS",status="200",uri="/user",} 1.0""")
            contains("""http_client_requests_seconds_sum{exception="none",host="example.com",method="GET",outcome="SUCCESS",status="200",uri="/user",} 0.0""") // maybe that the sum should start with '0.0' is too hard, may remove '0.0
            contains("""# HELP http_client_requests_seconds_max  
# TYPE http_client_requests_seconds_max gauge""")
            contains("""http_client_requests_seconds_max{exception="none",host="example.com",method="GET",outcome="SUCCESS",status="200",uri="/user",} 0.0""")
        }
    }

    @Test
    fun `Bad request POST call to url adds 400 CLIENT_ERROR metric`() = runTest {
        val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val engine = MockEngine {
            respondBadRequest()
        }

        val underTest = HttpClient(engine) {
            install(Metrics) {
                this.meterRegistry = prometheusRegistry
            }
        }

        underTest.post("https://example.com/user") { }

        val metrics = prometheusRegistry.scrape()

        assertThat(metrics).all {
            contains("""# HELP http_client_requests_seconds  
# TYPE http_client_requests_seconds summary""")
            contains("""http_client_requests_seconds_count{exception="none",host="example.com",method="POST",outcome="CLIENT_ERROR",status="400",uri="/user",} 1.0""")
            contains("""http_client_requests_seconds_sum{exception="none",host="example.com",method="POST",outcome="CLIENT_ERROR",status="400",uri="/user",} 0.0""") // maybe that the sum should start with '0.0' is too hard, may remove '0.0
            contains("""# HELP http_client_requests_seconds_max  
# TYPE http_client_requests_seconds_max gauge""")
            contains("""http_client_requests_seconds_max{exception="none",host="example.com",method="POST",outcome="CLIENT_ERROR",status="400",uri="/user",} 0.0""")
        }
    }

    @Test
    fun `Service unavailable PUT call to url adds 503 SERVER_ERROR metric`() = runTest {
        val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val engine = MockEngine {
            respondError(HttpStatusCode.ServiceUnavailable)
        }

        val underTest = HttpClient(engine) {
            install(Metrics) {
                this.meterRegistry = prometheusRegistry
            }
        }

        underTest.put("https://example.com/user") { }

        val metrics = prometheusRegistry.scrape()

        assertThat(metrics).all {
            contains("""# HELP http_client_requests_seconds  
# TYPE http_client_requests_seconds summary""")
            contains("""http_client_requests_seconds_count{exception="none",host="example.com",method="PUT",outcome="SERVER_ERROR",status="503",uri="/user",} 1.0""")
            contains("""http_client_requests_seconds_sum{exception="none",host="example.com",method="PUT",outcome="SERVER_ERROR",status="503",uri="/user",} 0.0""") // maybe that the sum should start with '0.0' is too hard, may remove '0.0
            contains("""# HELP http_client_requests_seconds_max  
# TYPE http_client_requests_seconds_max gauge""")
            contains("""http_client_requests_seconds_max{exception="none",host="example.com",method="PUT",outcome="SERVER_ERROR",status="503",uri="/user",} 0.0""")
        }
    }
}
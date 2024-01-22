package net.codinux.web.micrometer

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import net.codinux.web.micrometer.apachehttpclient5.ApacheHttpClient5JokeRepository
import net.codinux.web.micrometer.common.JokeRepository
import net.codinux.web.micrometer.javahttpclient.JavaHttpClientJokeRepository
import net.codinux.web.micrometer.okhttp.OkHttpJokeRepository

fun main() {
    val testApp = MetricsTestApp()

    println("OkHttp metrics:")
    println(testApp.getOkHttpMetrics())

    println("\nJava HttpClient metrics:")
    println(testApp.getJavaHttpClientMetrics())

    println("\nApache HttpClient 5 metrics:")
    println(testApp.getApacheHttpClient5Metrics())
}

class MetricsTestApp {

    fun getOkHttpMetrics() =
        getMetricsForHttpClient { registry -> OkHttpJokeRepository(registry) }

    fun getJavaHttpClientMetrics() =
        getMetricsForHttpClient { registry -> JavaHttpClientJokeRepository(registry) }

    fun getApacheHttpClient5Metrics() =
        getMetricsForHttpClient { registry -> ApacheHttpClient5JokeRepository(registry) }

    private fun getMetricsForHttpClient(repositoryCreator: (MeterRegistry) -> JokeRepository): String {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val repo = repositoryCreator(registry)
        val joke = repo.getJoke()

        return registry.scrape()
    }

}
package net.codinux.web.micrometer

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import net.codinux.web.micrometer.apachehttpclient5.ApacheHttpClient5JokeRepository
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

    fun getOkHttpMetrics(): String {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val repo = OkHttpJokeRepository(registry)
        val joke = repo.getJoke()

        return registry.scrape()
    }

    fun getJavaHttpClientMetrics(): String {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val repo = JavaHttpClientJokeRepository(registry)
        val joke = repo.getJoke()

        return registry.scrape()
    }

    fun getApacheHttpClient5Metrics(): String {
        val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        val repo = ApacheHttpClient5JokeRepository(registry)
        val joke = repo.getJoke()

        return registry.scrape()
    }

}
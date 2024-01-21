package net.codinux.web.micrometer

import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import net.codinux.web.micrometer.okhttp.OkHttpJokeRepository

fun main() {
    val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val okHttpRepo = OkHttpJokeRepository(registry)
    val joke = okHttpRepo.getJoke()

    println("OkHttp metrics:")
    println(registry.scrape())
}
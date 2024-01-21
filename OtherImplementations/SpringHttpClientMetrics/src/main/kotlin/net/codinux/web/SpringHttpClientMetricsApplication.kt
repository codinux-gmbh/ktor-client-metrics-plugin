package net.codinux.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringHttpClientMetricsApplication

fun main(args: Array<String>) {
	runApplication<SpringHttpClientMetricsApplication>(*args)
}
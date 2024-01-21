package net.codinux.web.micrometer.common

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.codinux.web.micrometer.model.Joke

object JacksonObjectMapper {

    private val objectMapper = ObjectMapper()
        .registerModules(KotlinModule.Builder().build())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    fun deserializeJoke(serializedJoke: String): Joke =
        objectMapper.readValue(serializedJoke, Joke::class.java)

}
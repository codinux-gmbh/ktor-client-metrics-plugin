package net.codinux.web.jokes

import net.codinux.web.jokes.model.Joke
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient

@Service
class JokeRepository(
    webClientBuilder: WebClient.Builder
) {

    // the RestClient doesn't seem to expose metrics
    private val jokeClient = RestClient.create()
        .get()
        .uri("https://v2.jokeapi.dev/joke/Programming?blacklistFlags=racist,sexist")

    private val webClient = webClientBuilder
        .baseUrl("https://v2.jokeapi.dev/joke/Programming?blacklistFlags=racist,sexist")
        .build()

    fun getJoke(): Joke? =
        //jokeClient.retrieve().body(Joke::class.java)
        webClient.get().retrieve().bodyToMono(Joke::class.java).block()

}
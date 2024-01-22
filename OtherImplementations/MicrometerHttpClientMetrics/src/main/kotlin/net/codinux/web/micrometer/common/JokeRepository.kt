package net.codinux.web.micrometer.common

import net.codinux.web.micrometer.model.Joke

interface JokeRepository {

    fun getJoke(): Joke?

}
package net.codinux.web.micrometer.common

object Constants {

    const val JokeApiHost = "https://v2.jokeapi.dev"
    const val JokeApiPath = "/joke/Programming?blacklistFlags=racist,sexist"
    const val JokeApiUrl = JokeApiHost + JokeApiPath

    const val NotExistingUrl = JokeApiHost + "/i_do_not_exist"

}
package net.codinux.web.jokes

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = [ "/joke" ])
class JokeController(
    private val repository: JokeRepository
) {

    @GetMapping
    fun getJoke() = repository.getJoke()

}
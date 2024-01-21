package net.codinux.web.jokes

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import net.codinux.web.jokes.model.Joke
import org.eclipse.microprofile.rest.client.RestClientBuilder
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.client.jaxrs.ResteasyClient
import java.net.URI

@Path("/joke")
class JokeResource {

    @RestClient
    protected lateinit var repository: JokeRepository


    @GET
    fun getRandomJoke(): Joke = repository.get()

}
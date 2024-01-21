package net.codinux.web.jokes

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import net.codinux.web.jokes.model.Joke
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@Path("")
@RegisterRestClient(configKey="joki-api")
interface JokeRepository {

    @GET
    fun get(): Joke

}
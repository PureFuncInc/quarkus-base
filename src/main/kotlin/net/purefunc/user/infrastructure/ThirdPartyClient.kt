package net.purefunc.user.infrastructure

import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@Path("/api")
@RegisterRestClient(configKey = "third-party")
interface ThirdPartyClient {

    @GET
    fun getById(@QueryParam("id") id: String): Uni<ThirdPartyResp>
}
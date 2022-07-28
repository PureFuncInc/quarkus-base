package net.purefunc.user.interfaces.web

import net.purefunc.common.response200
import net.purefunc.common.responseToken
import net.purefunc.user.interfaces.facade.UserFacade
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import org.jboss.resteasy.reactive.RestQuery
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@Path("/api/v1.0/users")
class UserController(
    private val userFacade: UserFacade,
) {

    @Path("/auth")
    @POST
    suspend fun loginOrSignup(memberLoginReqDTO: UserLoginReqDTO): Response =
        userFacade.loginOrSignup(memberLoginReqDTO).responseToken()

    @Path("/records")
    @GET
    suspend fun queryLoginRecords(@RestQuery email: String): Response =
        userFacade.queryLoginRecords(email).response200()
}
package net.purefunc.user.interfaces.web

import net.purefunc.common.response200
import net.purefunc.common.responseToken
import net.purefunc.user.interfaces.facade.UserFacade
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement
import org.jboss.resteasy.reactive.RestQuery
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Path("/api/v1.0/users")
@SecurityRequirement(name = "auth")
class UserController(
    private val userFacade: UserFacade,
) {

    @Path("/auth")
    @POST
    @PermitAll
    suspend fun loginOrSignup(memberLoginReqDTO: UserLoginReqDTO): Response =
        userFacade.loginOrSignup(memberLoginReqDTO).responseToken()

    @Path("/records")
    @GET
    @RolesAllowed(value = ["USER"])
    suspend fun queryLoginRecords(
        @Context securityContext: SecurityContext,
        @RestQuery email: String
    ): Response =
        apply {
            println(securityContext.userPrincipal.name)
        }.run {
            userFacade.queryLoginRecords(email).response200()
        }
}
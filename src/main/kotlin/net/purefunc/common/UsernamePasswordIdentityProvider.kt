package net.purefunc.common

import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import kotlinx.coroutines.runBlocking
import net.purefunc.user.infrastructure.dao.MemberDao
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UsernamePasswordIdentityProvider(
    private val memberDao: MemberDao,
) : IdentityProvider<UsernamePasswordAuthenticationRequest> {

    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> =
        UsernamePasswordAuthenticationRequest::class.java

    override fun authenticate(
        request: UsernamePasswordAuthenticationRequest?,
        context: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> {
        val username = request?.username
        val password = request?.password?.password
        if (username == null || password == null) {
            throw AuthenticationFailedException()
        }

        return runBlocking {
            val member = memberDao.findByEmail(username)
            val identity = QuarkusSecurityIdentity
                .builder()
                .setPrincipal { member!!.email }
                .addRole("USER")
                .build() as SecurityIdentity
            Uni.createFrom().item(identity)
        }
    }
}
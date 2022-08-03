package net.purefunc.common

import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import net.purefunc.user.infrastructure.dao.MemberDao
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.control.ActivateRequestContext

@ApplicationScoped
class UsernamePasswordIdentityProvider(
    private val memberDao: MemberDao,
) : IdentityProvider<UsernamePasswordAuthenticationRequest> {

    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> =
        UsernamePasswordAuthenticationRequest::class.java

    @ActivateRequestContext
    override fun authenticate(
        request: UsernamePasswordAuthenticationRequest?,
        context: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> =
        run {
            val username = request?.username ?: throw AuthenticationFailedException()
            val password = request.password?.password ?: throw AuthenticationFailedException()
            Pair(username, String(password))
        }.run {
            memberDao.findByEmailUni(first)
                .map {
                    it?.takeIf {
                        BcryptUtil.matches(second, it.password)
                    }?.run {
                        QuarkusSecurityIdentity
                            .builder()
                            .setPrincipal { it.email }
                            .addRole("USER")
                            .build() as SecurityIdentity
                    } ?: throw AuthenticationFailedException()
                }
        }
}
package net.purefunc.common

import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.TrustedAuthenticationRequest
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import net.purefunc.user.infrastructure.dao.MemberDao
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.control.ActivateRequestContext

@ApplicationScoped
class TrustedIdentityProvider(
    private val memberDao: MemberDao,
) : IdentityProvider<TrustedAuthenticationRequest> {
    override fun getRequestType(): Class<TrustedAuthenticationRequest> =
        TrustedAuthenticationRequest::class.java

    @ActivateRequestContext
    override fun authenticate(
        request: TrustedAuthenticationRequest?,
        context: AuthenticationRequestContext?
    ): Uni<SecurityIdentity> {
        val username = request?.principal ?: throw AuthenticationFailedException()

        return memberDao.findByEmailUni(username)
            .map {
                it?.run {
                    QuarkusSecurityIdentity
                        .builder()
                        .setPrincipal { email }
                        .addRole("USER")
                        .build() as SecurityIdentity
                } ?: throw AuthenticationFailedException()
            }
    }
}
package net.purefunc.user.application

import arrow.core.Either
import io.smallrye.mutiny.coroutines.awaitSuspending
import net.purefunc.common.KaqAppErr
import net.purefunc.common.QuarkusAppErr
import net.purefunc.kotlin.ext.AppErr
import net.purefunc.kotlin.ext.Slf4j.Companion.log
import net.purefunc.user.domain.UserDO
import net.purefunc.user.domain.service.UserDomainService
import net.purefunc.user.infrastructure.ThirdPartyClient
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import org.eclipse.microprofile.rest.client.inject.RestClient
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserApplicationService(
    @RestClient private val thirdPartyClient: ThirdPartyClient,
    private val userDomainService: UserDomainService,
) {

    suspend fun loginOrSignup(
        userLoginReqDTO: UserLoginReqDTO
    ): Either<QuarkusAppErr, UserDO> =
        apply {
//            log.info(thirdPartyClient.getById("1").awaitSuspending().toString())
        }.run {
            userDomainService.loginOrSignup(userLoginReqDTO)
        }

    suspend fun queryLoginRecordsByEmail(
        email: String
    ): Either<QuarkusAppErr, UserDO> =
        userDomainService.queryLoginRecordsByEmail(email)
}
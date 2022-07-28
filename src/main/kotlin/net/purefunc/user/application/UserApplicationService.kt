package net.purefunc.user.application

import arrow.core.Either
import net.purefunc.kotlin.ext.CustomErr
import net.purefunc.user.domain.UserDO
import net.purefunc.user.domain.service.UserDomainService
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserApplicationService(
    private val userDomainService: UserDomainService,
) {

    suspend fun loginOrSignup(
        userLoginReqDTO: UserLoginReqDTO
    ): Either<CustomErr, UserDO> =
        userDomainService.loginOrSignup(userLoginReqDTO)

    suspend fun queryLoginRecordsByEmail(
        email: String
    ): Either<CustomErr, UserDO> =
        userDomainService.queryLoginRecordsByEmail(email)
}
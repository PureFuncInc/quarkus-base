package net.purefunc.user.domain.service

import arrow.core.Either
import net.purefunc.common.KaqAppErr
import net.purefunc.common.QuarkusAppErr
import net.purefunc.kotlin.ext.AppErr
import net.purefunc.user.domain.UserDO
import net.purefunc.user.domain.repository.UserRepository
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserDomainService(
    private val userRepository: UserRepository,
) {

    suspend fun loginOrSignup(
        userLoginReqDTO: UserLoginReqDTO
    ): Either<QuarkusAppErr, UserDO> =
        userRepository
            .queryByEmail(userLoginReqDTO.email)
            .fold(
                ifLeft = { userRepository.signup(userLoginReqDTO) },
                ifRight = { userRepository.login(userLoginReqDTO.password, it) },
            )

    suspend fun queryLoginRecordsByEmail(
        email: String
    ): Either<QuarkusAppErr, UserDO> = userRepository.queryByEmail(email)
}
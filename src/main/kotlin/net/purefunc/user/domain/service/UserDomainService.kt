package net.purefunc.user.domain.service

import arrow.core.Either
import net.purefunc.kotlin.ext.CustomErr
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
    ): Either<CustomErr, UserDO> =
        userRepository
            .queryByEmail(userLoginReqDTO.email)
            .fold(
                ifLeft = { userRepository.signup(userLoginReqDTO) },
                ifRight = { userRepository.login(userLoginReqDTO.password, it) },
            )

    suspend fun queryLoginRecordsByEmail(
        email: String
    ): Either<CustomErr, UserDO> = userRepository.queryByEmail(email)
}
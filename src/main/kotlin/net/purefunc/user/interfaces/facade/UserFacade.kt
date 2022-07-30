package net.purefunc.user.interfaces.facade

import arrow.core.Either
import net.purefunc.common.QuarkusAppErr
import net.purefunc.kotlin.ext.AppErr
import net.purefunc.kotlin.ext.randomAlphanumeric
import net.purefunc.kotlin.ext.urlDecode
import net.purefunc.user.application.UserApplicationService
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import net.purefunc.user.interfaces.facade.resp.UserQueryRecordRespDTO
import org.jboss.resteasy.reactive.RestQuery
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserFacade(
    private val userApplicationService: UserApplicationService
) {

    suspend fun loginOrSignup(
        memberLoginReqDTO: UserLoginReqDTO
    ): Either<QuarkusAppErr, String> =
        userApplicationService
            .loginOrSignup(memberLoginReqDTO)
            .map {
                randomAlphanumeric(32)
            }

    suspend fun queryLoginRecords(
        @RestQuery email: String
    ): Either<QuarkusAppErr, UserQueryRecordRespDTO> =
        userApplicationService
            .queryLoginRecordsByEmail(email.urlDecode())
            .map {
                UserQueryRecordRespDTO(
                    nickName = it.memberInfoEntity.nickName,
                    email = it.memberInfoEntity.email,
                    loginRecords = it.loginRecords,
                )
            }
}
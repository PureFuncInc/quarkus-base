package net.purefunc.user.domain.repository

import arrow.core.Either
import io.quarkus.hibernate.reactive.panache.Panache
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import net.purefunc.common.KaqAppErr
import net.purefunc.kotlin.ext.CustomErr
import net.purefunc.kotlin.ext.catchErrWhenRun
import net.purefunc.kotlin.ext.catchErrWhenTrue
import net.purefunc.kotlin.ext.flatCatchErrWhenNull
import net.purefunc.kotlin.ext.flatCatchErrWhenRun
import net.purefunc.kotlin.ext.md5
import net.purefunc.kotlin.ext.unixTimeMilli
import net.purefunc.user.domain.UserDO
import net.purefunc.user.domain.entity.MemberInfoEntity
import net.purefunc.user.domain.vo.LoginRecordVO
import net.purefunc.user.infrastructure.dao.MemberDao
import net.purefunc.user.infrastructure.dao.MemberLoginRecordDao
import net.purefunc.user.infrastructure.po.MemberLoginRecordPO
import net.purefunc.user.infrastructure.po.MemberPO
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository(
    val memberDao: MemberDao,
    val memberLoginRecordDao: MemberLoginRecordDao,
) {

    suspend fun login(
        password: String,
        userDO: UserDO
    ): Either<CustomErr, UserDO> =
        userDO
            .catchErrWhenTrue(KaqAppErr.IdPasswordNotMapping) {
                password.md5() != it.memberInfoEntity.password
            }.flatCatchErrWhenRun(KaqAppErr.Runtime("map")) {
                val loginRecord = Panache.withTransaction {
                    memberLoginRecordDao.persist(
                        MemberLoginRecordPO(
                            id = null,
                            memberUuid = it.memberInfoEntity.uuid,
                            ipAddress = "0.0.0.0",
                            createDate = unixTimeMilli,
                        )
                    )
                }.awaitSuspending()

                it.copy(
                    loginRecords = it.loginRecords
                        .toMutableList()
                        .plus(
                            LoginRecordVO(
                                ipAddress = loginRecord.ipAddress,
                                createDate = loginRecord.createDate,
                            )
                        )
                )
            }

    suspend fun signup(
        memberLoginReqDTO: UserLoginReqDTO,
    ): Either<CustomErr, UserDO> =
        unixTimeMilli
            .catchErrWhenRun(KaqAppErr.Runtime("map")) {
                Panache.withTransaction {
                    Uni.combine().all().unis(
                        memberDao.persist(
                            MemberPO(
                                id = null,
                                uuid = it,
                                nickName = memberLoginReqDTO.email,
                                email = memberLoginReqDTO.email,
                                password = memberLoginReqDTO.password.md5(),
                                createDate = unixTimeMilli,
                            )
                        ),
                        memberLoginRecordDao.persist(
                            MemberLoginRecordPO(
                                id = null,
                                memberUuid = it,
                                ipAddress = "0.0.0.0",
                                createDate = unixTimeMilli,
                            )
                        ),
                    ).asTuple()
                }.awaitSuspending()
            }.map {
                UserDO(
                    memberInfoEntity = MemberInfoEntity(
                        uuid = it.item1.uuid,
                        nickName = it.item1.nickName,
                        email = it.item1.email,
                        password = it.item1.password,
                        createDate = it.item1.createDate,
                    ),
                    loginRecords = memberLoginRecordDao
                        .findAllByMemberUuid(it.item1.uuid)
                        .map { po -> LoginRecordVO(po.ipAddress, po.createDate) }
                )
            }

    suspend fun queryByEmail(email: String): Either<CustomErr, UserDO> =
        memberDao
            .catchErrWhenRun(KaqAppErr.Runtime("")) {
                it.findByEmail(email)
            }
            .flatCatchErrWhenNull(KaqAppErr.Runtime(""))
            .flatCatchErrWhenRun(KaqAppErr.Runtime("")) {
                UserDO(
                    memberInfoEntity = MemberInfoEntity(
                        uuid = it.uuid,
                        nickName = it.nickName,
                        email = email,
                        password = it.password,
                        createDate = it.createDate,
                    ),
                    loginRecords = memberLoginRecordDao
                        .findAllByMemberUuid(it.uuid)
                        .map { po -> LoginRecordVO(po.ipAddress, po.createDate) }
                )
            }
}
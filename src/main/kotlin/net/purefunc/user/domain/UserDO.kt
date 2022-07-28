package net.purefunc.user.domain

import net.purefunc.user.domain.entity.MemberInfoEntity
import net.purefunc.user.domain.vo.LoginRecordVO

data class UserDO(

    val memberInfoEntity: MemberInfoEntity,

    val loginRecords: List<LoginRecordVO>
)

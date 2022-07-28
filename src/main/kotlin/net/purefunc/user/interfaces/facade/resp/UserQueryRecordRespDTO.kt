package net.purefunc.user.interfaces.facade.resp

import net.purefunc.user.domain.vo.LoginRecordVO

data class UserQueryRecordRespDTO(

    val nickName: String,

    val email: String,

    val loginRecords: List<LoginRecordVO>
)

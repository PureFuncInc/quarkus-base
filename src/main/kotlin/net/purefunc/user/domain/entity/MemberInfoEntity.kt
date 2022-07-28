package net.purefunc.user.domain.entity

data class MemberInfoEntity(

    val uuid: Long,

    val nickName: String,

    val email: String,

    val password: String,

    val createDate: Long,
)

package net.purefunc.user.infrastructure.po

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "member")
data class MemberPO(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,

    var uuid: Long,

    var nickName: String,

    var email: String,

    var password: String,

    var createDate: Long,
)

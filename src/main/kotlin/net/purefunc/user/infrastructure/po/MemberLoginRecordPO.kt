package net.purefunc.user.infrastructure.po

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "member_login_record")
data class MemberLoginRecordPO(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,

    var memberUuid: Long,

    var ipAddress: String,

    var createDate: Long,
)

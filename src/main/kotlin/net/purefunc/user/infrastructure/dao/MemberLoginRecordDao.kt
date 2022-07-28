package net.purefunc.user.infrastructure.dao

import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import net.purefunc.user.infrastructure.po.MemberLoginRecordPO
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MemberLoginRecordDao : PanacheRepository<MemberLoginRecordPO> {

    suspend fun findAllByMemberUuid(memberUuid: Long): List<MemberLoginRecordPO> =
        list("memberUuid = ?1 ORDER BY createDate DESC", memberUuid)
            .awaitSuspending()
}
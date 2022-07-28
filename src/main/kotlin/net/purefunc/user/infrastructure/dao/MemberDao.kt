package net.purefunc.user.infrastructure.dao

import io.quarkus.hibernate.reactive.panache.PanacheRepository
import io.smallrye.mutiny.coroutines.awaitSuspending
import net.purefunc.user.infrastructure.po.MemberPO
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MemberDao : PanacheRepository<MemberPO> {

    suspend fun findByEmail(email: String): MemberPO? =
        find("email = ?1", email)
            .firstResult<MemberPO>()
            .awaitSuspending()
}
package net.purefunc.common

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer


class IntegrationTestResource : QuarkusTestResourceLifecycleManager {

    private val cache = GenericContainer("redis:6").withExposedPorts(6379)

    private val db = PostgreSQLContainer("postgres:13")
        .withInitScript("init.sql")

    override fun start(): MutableMap<String, String> {
        cache.start()
        db.start()
        return mutableMapOf(
            "quarkus.redis.hosts" to "redis://${cache.host}:${cache.firstMappedPort}",
            "quarkus.datasource.reactive.url" to "vertx-reactive:postgresql://${db.host}:${db.firstMappedPort}/${db.databaseName}",
            "quarkus.datasource.db-kind" to "postgresql",
            "quarkus.datasource.username" to db.username,
            "quarkus.datasource.password" to db.password,
        )
    }

    override fun stop() {
        cache.stop()
        db.stop()
    }
}
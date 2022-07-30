package net.purefunc.common

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import net.purefunc.kotlin.ext.md5
import net.purefunc.kotlin.ext.toJson
import net.purefunc.kotlin.ext.unixTimeMilli
import net.purefunc.user.infrastructure.ThirdPartyResp
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

class IntegrationTestResource : QuarkusTestResourceLifecycleManager {

    private val mockServer = MockWebServer()

    private val cache = GenericContainer("redis:6").withExposedPorts(6379)

    private val db = PostgreSQLContainer("postgres:13")
        .withInitScript("init.sql")

    private val dispatcher = object : Dispatcher() {

        override fun dispatch(request: RecordedRequest) =
            when {
                request.path?.contains("/api") ?: false ->
                    MockResponse().addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBody(ThirdPartyResp("1", unixTimeMilli).toJson())
                else -> MockResponse().setResponseCode(404)
            }
    }

    override fun start() =
        apply {
            mockServer.start(12345)
            mockServer.dispatcher = dispatcher
            cache.start()
            db.start()
        }.run {
            mutableMapOf(
                "quarkus.redis.hosts" to "redis://${cache.host}:${cache.firstMappedPort}",
                "quarkus.datasource.reactive.url" to
                        "vertx-reactive:postgresql://${db.host}:${db.firstMappedPort}/${db.databaseName}",
                "quarkus.datasource.db-kind" to "postgresql",
                "quarkus.datasource.username" to db.username,
                "quarkus.datasource.password" to db.password,
            )
        }

    override fun stop() {
        mockServer.shutdown()
        cache.stop()
        db.stop()
    }
}


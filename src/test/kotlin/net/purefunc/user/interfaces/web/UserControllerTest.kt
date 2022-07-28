package net.purefunc.user.interfaces.web

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import net.purefunc.common.IntegrationTestResource
import net.purefunc.kotlin.ext.toClass
import net.purefunc.kotlin.ext.toJson
import net.purefunc.kotlin.ext.urlEncode
import net.purefunc.user.interfaces.facade.req.UserLoginReqDTO
import net.purefunc.user.interfaces.facade.resp.UserQueryRecordRespDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.ws.rs.core.HttpHeaders

@QuarkusTest
@QuarkusTestResource(IntegrationTestResource::class)
class UserControllerTest {

    @Test
    fun postUsersAuth() {
        val token = given()
            .`when`()
            .contentType(ContentType.JSON)
            .body(UserLoginReqDTO("vincent@purefunc.net", "123456").toJson())
            .post("/api/v1.0/users/auth")
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .extract()
            .header(HttpHeaders.AUTHORIZATION)
        Assertions.assertTrue(token.isNotEmpty())

        val resp = given()
            .`when`()
            .contentType(ContentType.JSON)
            .param("email", "vincent@purefunc.net".urlEncode())
            .get("/api/v1.0/users/records")
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .extract()
            .body()
            .asString()
            .toClass(UserQueryRecordRespDTO::class.java)
        Assertions.assertEquals(1, resp.loginRecords.size)
    }
}
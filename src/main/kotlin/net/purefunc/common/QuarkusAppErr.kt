package net.purefunc.common

import arrow.core.Either
import net.purefunc.kotlin.emoji.Emoji3
import net.purefunc.kotlin.ext.AppErr
import net.purefunc.kotlin.ext.Slf4j.Companion.log
import net.purefunc.kotlin.ext.randomUUID
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

const val ERROR_CODE = "ERROR_CODE"
const val ERROR_LOG_UUID = "ERROR_LOG_UUID"

open class QuarkusAppErr(code: String, message: String): AppErr(code, message)

sealed class KaqAppErr {

    object IdPasswordNotMapping : QuarkusAppErr("E401001", "id password not mapping")

    class Runtime(memo: String) : QuarkusAppErr("E500001", "$memo, runtime err")
}

fun <T> Either<QuarkusAppErr, T>.responseToken(): Response =
    fold(
        ifLeft = { listOf(it).handle() },
        ifRight = { Response.ok(it).header(HttpHeaders.AUTHORIZATION, "Bearer $it").build() },
    )

fun <T> Either<QuarkusAppErr, T>.response200(): Response =
    fold(
        ifLeft = { listOf(it).handle() },
        ifRight = { Response.ok(it).build() },
    )

fun List<QuarkusAppErr>.handle(): Response =
    randomUUID
        .also {
            forEach { err ->
                log.error("${Emoji3.RED_EXCLAMATION_MARK} $it -> ${err.message}")
            }
        }.let {
            Response
                .status(Response.Status.fromStatusCode(this[0].code.substring(1, 4).toInt()))
                .header(ERROR_CODE, this[0].code)
                .header(ERROR_LOG_UUID, it)
                .build()
        }

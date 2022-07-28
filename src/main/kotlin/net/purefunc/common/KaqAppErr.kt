package net.purefunc.common

import arrow.core.Either
import net.purefunc.kotlin.emoji.Emoji3
import net.purefunc.kotlin.ext.CustomErr
import net.purefunc.kotlin.ext.randomUUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response

const val ERROR_CODE = "ERROR_CODE"
const val ERROR_LOG_UUID = "ERROR_LOG_UUID"

val log: Logger = LoggerFactory.getLogger(object {}.javaClass)

sealed class KaqAppErr {

    object IdPasswordNotMapping : CustomErr("E401001", "id password not mapping")

    class Runtime(memo: String) : CustomErr("E500001", "$memo, runtime err")
}

fun <T> Either<CustomErr, T>.responseToken(): Response =
    fold(
        ifLeft = { listOf(it).handle() },
        ifRight = { Response.ok(it).header(HttpHeaders.AUTHORIZATION, "Bearer $it").build() },
    )

fun <T> Either<CustomErr, T>.response200(): Response =
    fold(
        ifLeft = { listOf(it).handle() },
        ifRight = { Response.ok(it).build() },
    )

fun List<CustomErr>.handle(): Response =
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

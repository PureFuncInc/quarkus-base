package net.purefunc

import io.quarkus.elytron.security.common.BcryptUtil

fun main() {

    val bcryptHash: String = BcryptUtil.bcryptHash("string")
    println(bcryptHash)

}
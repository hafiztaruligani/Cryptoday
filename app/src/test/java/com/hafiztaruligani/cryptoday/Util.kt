package com.hafiztaruligani.cryptoday

import java.util.UUID

internal class Util {
    fun getRandomString(n: Int = 5): String {
        return UUID.randomUUID().toString().take(n)
    }
}

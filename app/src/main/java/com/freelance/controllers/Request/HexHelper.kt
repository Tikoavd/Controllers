package com.freelance.controllers.Request

import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

fun String.hexToBytes(): ByteArray {
    val input = lowercase(Locale.US)
    val n = length / 2
    val output = ByteArray(n)
    var l = 0
    for (k in 0 until n) {
        var c = input[l++]
        var b = (if (c >= 'a') (c - 'a' + 10) else ((c - '0') shl 4)).toByte()
        c = input[l++]
        b = b or (if (c >= 'a') c - 'a' + 10 else c - '0').toByte()
        output[k] = b
    }
    return output
}

fun ByteArray.bytesToHex(): String {
    val hexArray =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val hexChars = CharArray(size * 2)
    var v: Byte
    for (j in indices) {
        v = this[j] and 0xFF.toByte()
        hexChars[j * 2] = hexArray[v.toInt() ushr 4]
        hexChars[j * 2 + 1] = hexArray[(v and 0x0F).toInt()]
    }
    return String(hexChars)
}
@file:JvmName("Converter")

package com.qihoo.samples.twowaysample.util

/**
 * @author JasonXu
 * @date   2020/5/5
 */

fun fromTenthsToSeconds(tenths: Int): String {
    return if (tenths < 600) {
        String.format(".1f", tenths / 10f)
    } else {
        val minutes = (tenths / 10) / 60
        val seconds = (tenths / 10) % 60
        String.format("%d:%02d", minutes, seconds)
    }
}
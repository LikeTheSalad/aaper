package com.likethesalad.android.aaper.sample.test.utils

fun interface CallMeWithArg<T : Any> {
    fun callMe(arg: T)
}

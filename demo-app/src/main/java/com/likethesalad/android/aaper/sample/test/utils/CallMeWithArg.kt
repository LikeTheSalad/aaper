package com.likethesalad.android.aaper.sample.test.utils

interface CallMeWithArg<T : Any> {

    fun callMe(arg: T)
}
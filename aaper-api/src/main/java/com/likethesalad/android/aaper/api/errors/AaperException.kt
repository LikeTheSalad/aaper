package com.likethesalad.android.aaper.api.errors

/**
 * Base exception for all Aaper operations' exceptions.
 */
open class AaperException(message: String?, cause: Throwable?) : Exception(message, cause)
package com.likethesalad.android.aaper.errors

import com.likethesalad.android.aaper.Aaper
import com.likethesalad.android.aaper.api.errors.AaperException

/**
 * This exception is thrown whenever [Aaper.setUp] is called more than once.
 */
class AaperInitializedAlreadyException
    : AaperException(
    "Aaper can only get initialized once, please considering doing so in your " +
            "Application's onCreate in order to ensure it won't get re-initialized",
    null
)
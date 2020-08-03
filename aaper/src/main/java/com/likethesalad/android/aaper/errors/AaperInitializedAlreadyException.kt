package com.likethesalad.android.aaper.errors

import com.likethesalad.android.aaper.api.errors.AaperException

/**
 * Created by César Muñoz on 03/08/20.
 */
class AaperInitializedAlreadyException
    : AaperException(
    "Aaper can only get initialized once, please considering doing so in your " +
            "Application's onCreate in order to ensure it won't get re-initialized",
    null
)
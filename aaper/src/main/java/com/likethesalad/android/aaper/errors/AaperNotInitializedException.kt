package com.likethesalad.android.aaper.errors

import com.likethesalad.android.aaper.api.errors.AaperException

/**
 * This exception is thrown when an attempt to access Aaper is made before initializing it.
 */
class AaperNotInitializedException
    : AaperException(
    "Aaper is not initialized. You must call Aaper.initialize before performing this operation",
    null
)
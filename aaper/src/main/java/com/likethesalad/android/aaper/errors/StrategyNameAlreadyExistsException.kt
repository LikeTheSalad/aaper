package com.likethesalad.android.aaper.errors

import com.likethesalad.android.aaper.api.errors.AaperException
import com.likethesalad.android.aaper.defaults.DefaultRequestStrategyFactory

/**
 * This exception is thrown whenever there's an attempt to register a new Strategy in
 * [DefaultRequestStrategyFactory] which name is already registered.
 */
class StrategyNameAlreadyExistsException(val strategyName: String) :
    AaperException(
        "There is already a request strategy registered with the name $strategyName",
        null
    )
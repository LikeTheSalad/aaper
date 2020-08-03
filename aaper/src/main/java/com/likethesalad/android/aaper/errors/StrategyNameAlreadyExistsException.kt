package com.likethesalad.android.aaper.errors

import com.likethesalad.android.aaper.api.errors.AaperException

/**
 * Created by César Muñoz on 29/07/20.
 */
class StrategyNameAlreadyExistsException(strategyName: String) :
    AaperException(
        "There is already a request strategy registered with the name $strategyName",
        null
    )
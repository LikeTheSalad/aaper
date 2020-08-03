package com.likethesalad.android.aaper.api.errors

/**
 * Created by César Muñoz on 29/07/20.
 */
class HandlerNameAlreadyExistsException(handlerName: String) :
    AaperException(
        "There is already a permission request handler registered with the name $handlerName",
        null
    )
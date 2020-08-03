package com.likethesalad.android.aaper.api.errors

/**
 * Created by César Muñoz on 01/08/20.
 */
class RequestExecutedAlreadyException(permissions: List<String>) :
    AaperException("Request for permissions $permissions has been executed already", null)
package com.likethesalad.android.aaper.api.errors

/**
 * Exception thrown when a request was previously launched and there was an attempt
 * to launch it again.
 */
class RequestExecutedAlreadyException(permissions: List<String>) :
    AaperException("Request for permissions $permissions has been executed already", null)
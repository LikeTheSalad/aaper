package com.likethesalad.android.aaper.base.strategies

import com.likethesalad.android.aaper.api.base.LaunchMetadata
import com.likethesalad.android.aaper.api.strategy.RequestStrategy
import com.likethesalad.android.aaper.data.RequestCodeLaunchMetadata

/**
 * Base [RequestStrategy] for common Android component requests in which a numeric code
 * must be provided as identifier of the request.
 */
abstract class RequestWithCodeMetadataStrategy<T> : RequestStrategy<T>() {

    companion object {
        private const val DEFAULT_REQUEST_CODE = 21293
    }

    override fun getLaunchMetadata(host: T): LaunchMetadata? {
        return RequestCodeLaunchMetadata(getRequestCode())
    }

    open fun getRequestCode(): Int {
        return DEFAULT_REQUEST_CODE
    }
}
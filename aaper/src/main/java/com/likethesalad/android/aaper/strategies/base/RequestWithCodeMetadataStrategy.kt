package com.likethesalad.android.aaper.strategies.base

import com.likethesalad.android.aaper.api.base.LaunchMetadata
import com.likethesalad.android.aaper.api.base.RequestStrategy
import com.likethesalad.android.aaper.launchmetadata.RequestCodeLaunchMetadata

/**
 * Created by César Muñoz on 13/08/20.
 */
abstract class RequestWithCodeMetadataStrategy<T> : RequestStrategy<T>() {

    companion object {
        private const val DEFAULT_REQUEST_CODE = 1202
    }

    override fun getLaunchMetadata(host: T): LaunchMetadata? {
        return RequestCodeLaunchMetadata(getRequestCode())
    }

    open fun getRequestCode(): Int {
        return DEFAULT_REQUEST_CODE
    }
}
package com.likethesalad.android.aaper.data

import com.likethesalad.android.aaper.api.base.LaunchMetadata

/**
 * This is the [LaunchMetadata] used for common android permissions requests in which a code
 * must be provided as an identifier of the request.
 */
class RequestCodeLaunchMetadata(val code: Int) : LaunchMetadata {

    override fun isEqualTo(other: LaunchMetadata?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is RequestCodeLaunchMetadata) {
            return false
        }

        return other.code == code
    }
}
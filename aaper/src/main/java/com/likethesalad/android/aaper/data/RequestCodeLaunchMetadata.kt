package com.likethesalad.android.aaper.data

import com.likethesalad.android.aaper.api.base.LaunchMetadata

/**
 * Created by César Muñoz on 13/08/20.
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
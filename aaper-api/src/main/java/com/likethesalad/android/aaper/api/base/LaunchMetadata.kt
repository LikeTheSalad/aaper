package com.likethesalad.android.aaper.api.base

/**
 * It represents any sort of data that might be needed for a permission request. For example,
 * it can hold a requestCode for common permission requests.
 */
interface LaunchMetadata {

    /**
     * Checks if another [LaunchMetadata] is equal to self.
     */
    fun isEqualTo(other: LaunchMetadata?): Boolean
}
package com.likethesalad.android.aaper.api.statusprovider

/**
 * This class should tell whether a given permission is granted not.
 */
abstract class PermissionStatusProvider<T> {

    @Suppress("UNCHECKED_CAST")
    internal fun internalIsPermissionGranted(host: Any, permissionName: String): Boolean {
        return isPermissionGranted(host as T, permissionName)
    }

    /**
     * @param host - The class that contains the original method, e.g. Activity or Fragment.
     * @param permissionName - The permission being queried.
     *
     * @return - TRUE if the [permissionName] is granted, FALSE otherwise.
     */
    abstract fun isPermissionGranted(host: T, permissionName: String): Boolean
}
package com.likethesalad.android.aaper.base.fragment.statusprovider

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.statusprovider.PermissionStatusProvider

/**
 * This class queries a permission granted status using Fragment as host.
 */
class FragmentPermissionStatusProvider : PermissionStatusProvider<Fragment>() {

    override fun isPermissionGranted(host: Fragment, permissionName: String): Boolean {
        return ContextCompat.checkSelfPermission(
            host.requireContext(),
            permissionName
        ) == PackageManager.PERMISSION_GRANTED
    }
}
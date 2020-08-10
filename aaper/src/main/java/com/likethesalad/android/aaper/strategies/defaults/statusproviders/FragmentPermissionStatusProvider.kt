package com.likethesalad.android.aaper.strategies.defaults.statusproviders

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.PermissionStatusProvider

/**
 * Created by César Muñoz on 10/08/20.
 */
class FragmentPermissionStatusProvider : PermissionStatusProvider<Fragment>() {

    override fun isPermissionGranted(host: Fragment, permissionName: String): Boolean {
        return ContextCompat.checkSelfPermission(
            host.requireContext(),
            permissionName
        ) == PackageManager.PERMISSION_GRANTED
    }
}
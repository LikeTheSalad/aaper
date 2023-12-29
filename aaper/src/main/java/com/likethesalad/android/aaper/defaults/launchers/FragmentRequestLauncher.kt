package com.likethesalad.android.aaper.defaults.launchers

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.base.launchers.RequestWithCodeLauncher

/**
 * This class launches a request permission using a Fragment as host.
 */
@Suppress("DEPRECATION")
class FragmentRequestLauncher : RequestWithCodeLauncher<Fragment>() {

    override fun launchPermissionsRequest(
        host: Fragment,
        permissions: List<String>,
        requestCode: Int
    ) {
        host.requestPermissions(permissions.toTypedArray(), requestCode)
    }
}
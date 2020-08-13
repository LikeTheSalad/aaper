package com.likethesalad.android.aaper.strategies.defaults.launchers

import androidx.fragment.app.Fragment

/**
 * This class launches a request permission using a Fragment as host.
 */
class FragmentRequestLauncher : RequestWithCodeLauncher<Fragment>() {

    override fun launchPermissionsRequest(
        host: Fragment,
        permissions: List<String>,
        requestCode: Int
    ) {
        host.requestPermissions(permissions.toTypedArray(), requestCode)
    }
}
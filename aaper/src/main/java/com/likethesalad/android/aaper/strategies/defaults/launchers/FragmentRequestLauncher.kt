package com.likethesalad.android.aaper.strategies.defaults.launchers

import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.base.RequestLauncher

/**
 * Created by César Muñoz on 03/08/20.
 */
class FragmentRequestLauncher : RequestLauncher<Fragment>() {

    override fun launchPermissionsRequest(
        host: Fragment,
        permissions: List<String>,
        requestCode: Int
    ) {
        host.requestPermissions(permissions.toTypedArray(), requestCode)
    }
}
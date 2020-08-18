package com.likethesalad.android.aaper.sample

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.EnsurePermissions
import kotlinx.android.synthetic.main.my_fragment.*

/**
 * Created by César Muñoz on 18/08/20.
 */
class MyFragment : Fragment(R.layout.my_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn.setOnClickListener {
            searchMyLocation()
        }
    }

    @EnsurePermissions(
        permissions = [Manifest.permission.ACCESS_FINE_LOCATION]
    )
    private fun searchMyLocation() {
        Toast.makeText(
            requireContext(), "Location permission granted",
            Toast.LENGTH_SHORT
        ).show()
    }
}
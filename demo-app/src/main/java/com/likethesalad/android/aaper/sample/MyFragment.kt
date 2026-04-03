package com.likethesalad.android.aaper.sample

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.likethesalad.android.aaper.api.EnsurePermissions

/**
 * Created by César Muñoz on 18/08/20.
 */
class MyFragment : Fragment(R.layout.my_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btn).setOnClickListener {
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
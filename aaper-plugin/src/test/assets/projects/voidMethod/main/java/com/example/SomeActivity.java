package com.example;

import android.Manifest;
import com.likethesalad.android.aaper.api.EnsurePermissions;

public class SomeActivity extends android.app.Activity {

    @EnsurePermissions(permissions = {Manifest.permission.CAMERA})
    public void someMethod() {
        System.out.println("Permissions granted.");
    }
}
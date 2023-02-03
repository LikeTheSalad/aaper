package com.likethesalad.android.aaper.sample.test;

import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import com.likethesalad.android.aaper.api.EnsurePermissions;
import com.likethesalad.android.aaper.sample.test.utils.CallMe;

public class JavaActivity extends AppCompatActivity {

    @EnsurePermissions(permissions = {Manifest.permission.CAMERA})
    public void someMethod(CallMe callMe) {
        callMe.call();
    }
}

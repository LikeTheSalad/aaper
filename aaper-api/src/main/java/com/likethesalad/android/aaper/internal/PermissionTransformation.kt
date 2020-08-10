package com.likethesalad.android.aaper.internal

import android.app.Activity
import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.internal.interceptors.ReceivesRequestResultInterceptor
import com.likethesalad.android.aaper.internal.interceptors.RequiresPermissionInterceptor
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith
import net.bytebuddy.matcher.ElementMatchers.named

/**
 * Created by César Muñoz on 25/07/20.
 */

class PermissionTransformation : Plugin {

    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription,
        classFileLocator: ClassFileLocator
    ): DynamicType.Builder<*> {
        return builder.method(isAnnotatedWith(EnsurePermissions::class.java))
            .intercept(MethodDelegation.to(RequiresPermissionInterceptor::class.java))
            .method(named("onRequestPermissionsResult"))
            .intercept(MethodDelegation.to(ReceivesRequestResultInterceptor::class.java))
    }

    override fun close() {
        // Nothing to close
    }

    override fun matches(target: TypeDescription): Boolean {
        return target.isAssignableTo(Activity::class.java)
    }
}
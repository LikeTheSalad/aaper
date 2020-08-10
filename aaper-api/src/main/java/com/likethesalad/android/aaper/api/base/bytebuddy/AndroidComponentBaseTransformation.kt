package com.likethesalad.android.aaper.api.base.bytebuddy

import com.likethesalad.android.aaper.api.EnsurePermissions
import com.likethesalad.android.aaper.api.base.bytebuddy.interceptors.ReceivesRequestResultInterceptor
import com.likethesalad.android.aaper.api.base.bytebuddy.interceptors.RequiresPermissionInterceptor
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import net.bytebuddy.implementation.MethodDelegation
import net.bytebuddy.matcher.ElementMatchers

/**
 * This is the base ByteBuddy plugin for android components' permission-related
 * transformations.
 *
 * It should allow to attach Aaper to any class that has an
 * "onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)"
 * method.
 */

abstract class AndroidComponentBaseTransformation : Plugin {

    override fun apply(
        builder: DynamicType.Builder<*>,
        typeDescription: TypeDescription,
        classFileLocator: ClassFileLocator
    ): DynamicType.Builder<*> {
        return builder.method(ElementMatchers.isAnnotatedWith(EnsurePermissions::class.java))
            .intercept(MethodDelegation.to(RequiresPermissionInterceptor::class.java))
            .method(ElementMatchers.named("onRequestPermissionsResult"))
            .intercept(MethodDelegation.to(ReceivesRequestResultInterceptor::class.java))
    }

    override fun matches(target: TypeDescription): Boolean {
        return target.isAssignableTo(getHostClassType())
    }

    override fun close() {
        // Nothing to close by default.
    }

    /**
     * This function must return the type of Android component class
     * to which Aaper will be attached to. e.g. Activity.
     */
    abstract fun getHostClassType(): Class<out Any>

}
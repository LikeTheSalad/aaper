package com.likethesalad.android.aaper.plugin.appender.visitor.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class ClassNameTest {

    @Test
    fun `Verify class names`() {
        val packageName = "com.example"
        val simpleName = "SomeClass"
        val className = ClassName(packageName, simpleName)

        assertThat(className.packageName).isEqualTo(packageName)
        assertThat(className.simpleName).isEqualTo(simpleName)
        assertThat(className.internalName).isEqualTo("com/example/SomeClass")
        assertThat(className.fullName).isEqualTo("com.example.SomeClass")
        assertThat(className.fileName).isEqualTo("com/example/SomeClass.class")
    }

    @Test
    fun `Verify class names for empty package names`() {
        val packageName = ""
        val simpleName = "SomeClass"
        val className = ClassName(packageName, simpleName)

        assertThat(className.packageName).isEqualTo(packageName)
        assertThat(className.simpleName).isEqualTo(simpleName)
        assertThat(className.internalName).isEqualTo("SomeClass")
        assertThat(className.fullName).isEqualTo("SomeClass")
        assertThat(className.fileName).isEqualTo("SomeClass.class")
    }
}
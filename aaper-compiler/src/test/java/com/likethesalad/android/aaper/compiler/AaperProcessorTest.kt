package com.likethesalad.android.aaper.compiler

import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class AaperProcessorTest {

    @Test
    fun `When annotated method doesn't return void then raise an error`() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(
                JavaFileObjects.forSourceString(
                    "com.example.MyClass", """
                class MyClass {
                    @com.likethesalad.android.aaper.api.EnsurePermissions(permissions={"somePermission"})
                    public String someMethod() {
                        return "Something";
                    }
                }
            """.trimIndent()
                )
            )
        assertThat(compile).failed()
    }
}
package com.likethesalad.android.aaper.compiler

import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.JavaFileObjects
import org.junit.Test

class AaperProcessorTest {

    @Test
    fun checkProcessor() {
        val compile = javac()
            .withProcessors(AaperProcessor())
            .compile(JavaFileObjects.forResource("HelloWorld.java"))
        assertThat(compile).succeeded()
    }
}
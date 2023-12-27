package com.likethesalad.android.aaper.plugin.appender.visitor

import com.google.common.truth.Truth.assertThat
import com.likethesalad.android.aaper.internal.compiler.AaperRunnable
import com.likethesalad.android.aaper.plugin.appender.visitor.testutils.GeneratedClassLoader
import com.likethesalad.android.aaper.plugin.appender.visitor.utils.ClassName
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.objectweb.asm.Type
import org.objectweb.asm.util.CheckClassAdapter

class WraaperClassCreatorTest {

    @Before
    fun setUp() {
        WraaperClassCreator.classVisitorInterceptor = {
            CheckClassAdapter(it)
        }
    }

    @Test
    fun `Verify zero param method`() {
        val className = ClassName("com.example", "SomeName")
        val target = ZeroParamType()
        val generated =
            WraaperClassCreator.create(className, "callMe", Type.getType(ZeroParamType::class.java))

        val instance = getGeneratedInstance(className, generated, target)

        instance.run()
        assertThat(target.callCount).isEqualTo(1)
    }

    @Test
    fun `Verify one param method`() {
        val className = ClassName("com.example", "SomeName")
        val target = OneParamType()
        val generated =
            WraaperClassCreator.create(
                className,
                "callMe",
                Type.getType(OneParamType::class.java),
                Type.getType(String::class.java)
            )

        val paramValue = "The param value"
        val instance = getGeneratedInstance(className, generated, target, paramValue)

        instance.run()
        assertThat(target.callCount).isEqualTo(1)
        assertThat(target.storedParam).isEqualTo(paramValue)
    }

    @Test
    fun `Verify multiple param method`() {
        val className = ClassName("com.example", "SomeName")
        val target = TwoParamType()
        val generated =
            WraaperClassCreator.create(
                className,
                "callMe",
                Type.getType(TwoParamType::class.java),
                Type.getType(String::class.java),
                Type.DOUBLE_TYPE
            )

        val paramValue1 = "The param value"
        val paramValue2 = 2.0
        val instance = getGeneratedInstance(className, generated, target, paramValue1, paramValue2)

        instance.run()
        assertThat(target.callCount).isEqualTo(1)
        assertThat(target.storedParam1).isEqualTo(paramValue1)
        assertThat(target.storedParam2).isEqualTo(paramValue2)
    }

    private fun getGeneratedInstance(
        name: ClassName,
        generated: ByteArray,
        vararg params: Any
    ): AaperRunnable {
        val clazz = GeneratedClassLoader(javaClass.classLoader).defineClass(
            name.fullName,
            generated
        )
        val classParams = params.map {
            val javaClass = it.javaClass
            javaClass.kotlin.javaPrimitiveType ?: javaClass
        }.toTypedArray()
        return clazz.getDeclaredConstructor(*classParams).newInstance(*params) as AaperRunnable
    }

    class ZeroParamType {
        var callCount = 0
            private set

        fun callMe() {
            callCount++
        }
    }

    class OneParamType {
        var storedParam = ""

        var callCount = 0
            private set

        fun callMe(param: String) {
            callCount++
            storedParam = param
        }
    }

    class TwoParamType {
        var storedParam1 = ""
        var storedParam2 = 0.0

        var callCount = 0
            private set

        fun callMe(param1: String, param2: Double) {
            callCount++
            storedParam1 = param1
            storedParam2 = param2
        }
    }
}
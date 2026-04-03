package com.likethesalad.tools.functional.testing.android.descriptor

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem
import java.io.File

class AndroidAppProjectDescriptor(
    name: String,
    inputDir: File,
    blockItems: List<GradleBlockItem> = emptyList(),
    namespace: String = "some.localpackage.localname.forlocal.$name",
    compileSdkVersion: Int = 36
) : AndroidProjectDescriptor(
    name = name,
    inputDir = inputDir,
    pluginId = "com.android.application",
    blockItems = blockItems,
    namespace = namespace,
    compileSdkVersion = compileSdkVersion
)

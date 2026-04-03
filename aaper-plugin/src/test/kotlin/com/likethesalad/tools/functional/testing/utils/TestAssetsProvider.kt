package com.likethesalad.tools.functional.testing.utils

import java.io.File
import java.nio.file.Paths

class TestAssetsProvider(
    private val assetsFolderName: String
) {
    private var testsDirName: String = "test"

    private val functionalAssetsDir: File by lazy {
        Paths.get("src", testsDirName, "assets", assetsFolderName).toFile()
    }

    fun withTestsDirName(testsDirName: String): TestAssetsProvider {
        this.testsDirName = testsDirName
        return this
    }

    fun getFile(relativePath: String): File {
        val asset = File(functionalAssetsDir, relativePath)
        require(asset.exists()) { "Asset '$asset' not found in '$assetsFolderName'" }
        return asset
    }
}

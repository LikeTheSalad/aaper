plugins {
    id("org.jetbrains.kotlin.jvm")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

dependencies {
    testImplementation(platform(libs.findLibrary("junit-bom").get()))
    testImplementation(libs.findLibrary("junit-jupiter").get())
    testRuntimeOnly(libs.findLibrary("junit-launcher").get())
    testImplementation(libs.findLibrary("assertj").get())
    testImplementation(libs.findLibrary("mockk").get())
}

tasks.withType<Test> {
    useJUnitPlatform()
}

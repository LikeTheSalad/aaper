plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.artifactPublisher)
}

description = "Ensure Android runtime permissions using annotations only."

artifactPublisher {
    displayName = "Aaper"
    url = "https://github.com/LikeTheSalad/aaper"
    vcsUrl = "https://github.com/LikeTheSalad/aaper.git"
    issueTrackerUrl = "https://github.com/LikeTheSalad/aaper/issues"
    tags.addAll("android", "permissions", "annotation", "runtime")
}

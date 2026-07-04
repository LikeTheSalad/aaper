Change Log
==========

<!-- CHANGELOG_INSERT -->

## Version 3.1.3 (2026-07-01)

* Update dependency org.junit:junit-bom to v6.1.1 ([#62](https://github.com/LikeTheSalad/aaper/pull/62))
* Update Gradle to v9.6.1 ([#61](https://github.com/LikeTheSalad/aaper/pull/61))
* Update Gradle to v9.6.0 ([#60](https://github.com/LikeTheSalad/aaper/pull/60))
* Update plugin buildConfig to v6.0.10 ([#58](https://github.com/LikeTheSalad/aaper/pull/58))

## Version 3.1.2 (2026-06-01)

* Update dependency io.mockk:mockk to v1.14.11 ([#55](https://github.com/LikeTheSalad/aaper/pull/55))
* Update asm to v9.10.1 ([#54](https://github.com/LikeTheSalad/aaper/pull/54))
* Update dependency org.junit:junit-bom to v6.1.0 ([#53](https://github.com/LikeTheSalad/aaper/pull/53))
* Update asm to v9.10 ([#52](https://github.com/LikeTheSalad/aaper/pull/52))
* Update Gradle to v9.5.1 ([#51](https://github.com/LikeTheSalad/aaper/pull/51))
* Update android to v9.2.1 ([#50](https://github.com/LikeTheSalad/aaper/pull/50))

## Version 3.1.1 (2026-05-01)

* Update Gradle to v9.5.0 ([#47](https://github.com/LikeTheSalad/aaper/pull/47))
* Update dependency org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin to v2.3.21 ([#46](https://github.com/LikeTheSalad/aaper/pull/46))
* Update android to v9.2.0 ([#45](https://github.com/LikeTheSalad/aaper/pull/45))
* Update android to v9.1.1 ([#44](https://github.com/LikeTheSalad/aaper/pull/44))
* Adding permissions ([#43](https://github.com/LikeTheSalad/aaper/pull/43))
* Adding final checks job ([#42](https://github.com/LikeTheSalad/aaper/pull/42))
* Automated release ([#41](https://github.com/LikeTheSalad/aaper/pull/41))

## Version 3.1.0 (2026-04-04)

* Bumping up dependencies
* Setting up GH Workflows
* Adding tests

## Version 3.0.0 *(02-01-2024)*

* Removed annotation processor module.
* Creating wraapers using the AGP forScope transformation API.
* BREAKING - Simplified Aaper's API by:
    * Allowing to pass strategies to the EnsurePermissions annotations as class types rather than
      with a name, hence removing the need to name every new strategy.
    * Avoiding having to register new strategies before using them (they will be automatically
      instantiated on demand).
    * Providing the `RequestStrategyFactory` interface to allow for custom instantiation of
      strategies if needed.

## Version 2.1.0 *(05-02-2023)*

* Using androidx.startup to automatically initialize Aaper with its default config (thanks
  @msasikanth)

## Version 2.0.0 *(04-02-2023)*


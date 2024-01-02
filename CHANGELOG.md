Change Log
==========

Version 3.0.0 *(02-01-2024)*
---

* Removed annotation processor module.
* Creating wraapers using the AGP forScope transformation API.
* BREAKING - Simplified Aaper's API by:
    * Allowing to pass strategies to the EnsurePermissions annotations as class types rather than
      with a name, hence removing the need to name every new strategy.
    * Avoiding having to register new strategies before using them (they will be automatically
      instantiated on demand).
    * Providing the `RequestStrategyFactory` interface to allow for custom instantiation of
      strategies if needed.

Version 2.1.0 *(05-02-2023)*
---

* Using androidx.startup to automatically initialize Aaper with its default config (thanks
  @msasikanth)

Version 2.0.0 *(04-02-2023)*
---

* Using a combination of annotation processor + the new AGP Instrumentation API
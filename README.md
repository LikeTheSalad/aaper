![version](https://img.shields.io/maven-central/v/com.likethesalad.android/aaper)

# Aaper

Table of Contents
=================  

* [What it is](#what-it-is)
    * [Default behavior example](#default-behavior-example)
* [Hot to use](#how-to-use)
* [Changing the default behavior](#changing-the-default-behavior)
    * [Custom Strategy example](#custom-strategy-example)
    * [Using our custom Strategy](#using-our-custom-strategy)
    * [Changing the pre-request behavior](#changing-the-pre-request-behavior)
* [Adding Aaper into your project](#adding-aaper-into-your-project)
    * [Prerequisites](#prerequisites)
    * [Aaper Gradle dependency](#aaper-gradle-dependency)
* [Troubleshooting](#troubleshooting)
* [Advanced configuration](#advanced-configuration)
    * [Creating a custom RequestStrategyProvider](#creating-a-custom-requeststrategyprovider)
    * [Overriding permission's status query and request launch](#overriding-permissions-status-query-and-request-launch)
* [License](#license)

What it is
---  
**A**nnotated **A**ndroid **Per**missions takes care of ensuring Android runtime permissions for
an `EnsurePermissions`-annotated method inside either an Activity or a Fragment. The idea is to do
so without having to write any code nor override any Activity and/or Fragment method related to
runtime permission requests.

### Default behavior example

```kotlin  
// Aaper initialization  
package my.app

class MyApplication {  
  
    override fun onCreate() {  
        super.onCreate()  
        Aaper.init()// This must be called only once, therefore the Application.onCreate method is a great place to do so.
    }  
}  
```  

```xml
<!--Your AndroidManifest.xml-->

<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!--This is very important, the Android OS will ignore any permission request for permissions not-->
    <!--listed in your manifest.-->
    <uses-permission android:name="android.permission.CAMERA" />

    <!--You need to add your application class (shown above) to the manifest too as shown below-->
    <application android:name="my.app.MyApplication">
        <!--yada yada...-->
    </application>
</manifest>
```

```kotlin  
// Aaper usage  

class MyActivity/MyFragment {  
  
    override fun onCreate/onViewCreated(...) {  
        takePhotoButton.setOnClickListener {  
            takePhoto()  
        }  
    }  
  
    @EnsurePermissions(permissions = [Manifest.permission.CAMERA])  
    fun takePhoto() {  
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()  
    }  
}  
```  

Just by adding the `EnsurePermissions` annotation to `takePhoto()`, what will happen (by default)
when we run that code and click on the `takePhotoButton` button is:

- Aaper will check if your app has the CAMERA permission already granted.
- If already granted, Aaper will proceed to run `takePhoto()` right away and it will all end there.
- If NOT granted, Aaper will NOT run `takePhoto()` right away, and rather will proceed to run the
  default permission `RequestStrategy` which is to launch the System's permission dialog asking the
  user to grant the CAMERA permission.
- If the user approves it, Aaper will proceed to run `takePhoto()`.
- If the user denies it, the default behavior is just not running `takePhoto()`.

Aaper's default behavior can be easily changed if you wanted to, you can find more details below
under `Changing the default behavior`.

How to use
---  
As we could see above in the default behavior example, there are only two things we need to do in
order to use Aaper into our Activities or Fragments:

- **Step one:** Make sure that the permissions you'll request with Aaper **are defined in
  your** `AndroidManifest.xml` **file too**. If you attempt to request a permission at runtime that
  isn't in your manifest, the OS will silently ignore your request.
- **Step two:** Initialize Aaper, this can be done by calling `Aaper.init()` only once, therefore a
  great place to do it is in your app's `Application.onCreate` method, as shown in the example
  above.
- **Step three:** Annotate an Activity or Fragment method with the `@EnsurePermissions` annotation
  where you provide a list of permissions (that are also defined in your `AndroidManifest.xml`) that
  such method needs in order to work properly. Alternatively, you can also pass an optional
  parameter named `strategyName`, where you can specify the behavior of handling such permissions'
  request. More info below under `Changing the default behavior`.

That's it, if you want to know how to modify Aaper's behavior to suit your needs, take a look
at `Changing the default behavior`.

> It is very important to bear in mind that, the @EnsurePermissions annotation only works on methods inside either an `Activity` or a` Fragment`, more specifically, an `androidx.fragment.app.Fragment` Fragment. Any @EnsurePermissions annotated method that isn't inside of either an Activity or a Fragment will be ignored.

Changing the default behavior
---  
Aaper's permission requests behavior is fully customizable, you can define what to do before and
after a permission request is executed, and even how the request is executed, by creating your
own `RequestStrategy` class. The way Aaper works is by delegating the request actions to
a `RequestStrategy` instance, you can tell Aaper which strategy to use by:

- Specifying the strategy name on the @EnsurePermissions annotation.
- Defining your own `RequestStrategy` as default.

### Custom Strategy example

We'll create a custom strategy that will finish the host Activity in the case that at least one of
the permissions requested by Aaper is denied.

We'll start by creating our class that extends from `ActivityRequestStrategy`:

```kotlin  
class FinishActivityOnDeniedStrategy : ActivityRequestStrategy() {  
  
    override fun onPermissionsRequestResults(  
        host: Activity,  
        data: PermissionsResult  
    ): Boolean {  
        TODO("Not yet implemented")  
    }  
  
    override fun getName(): String {  
        TODO("Not yet implemented")  
    }  
}  
```  

There are three types of `RequestStrategy` base classes that we can choose from when creating our
custom `RequestStrategy`, those are:

- `ActivityRequestStrategy` - Only supports EnsurePermissions-annotated methods inside Activities.
- `FragmentRequestStrategy` - Only supports EnsurePermissions-annotated methods inside Fragments.
- `AllRequestStrategy` - Supports both Activities and Fragment classes' EnsurePermissions-annotated
  methods.

  All three have the same structure and same base methods, the main difference from an
  implementation point of view, would be the type of `host` provided in their base functions, for
  example in the method `onPermissionsRequestResults` we see that our host is of type `Activity`,
  because we extend from `ActivityRequestStrategy`, whereas if we extend
  from `FragmentRequestStrategy`, the host will be a `Fragment`. For `AllRequestStrategy`, the host
  is `Any` or `Object` and you'd have to check its type manually in order to verify whether the
  current request is for an Activity or a Fragment.

In this example, we will annotate a method inside an Activity and nowhere else,
therefore `ActivityRequestStrategy` seems to suit better for this case.

We must provide for every custom `RequestStrategy` two things, a name (which will serve as an ID for
our Strategy) and a boolean as response for `onPermissionsRequestResults` method, depending on what
we return there, this is what will happen after a permission request is executed:

- If `onPermissionsRequestResults` returns TRUE, it means that the request was successful in our
  Strategy and therefore the EnsurePermissions-annotated method will get executed.
- If `onPermissionsRequestResults` returns FALSE, it means that the request failed in our Strategy
  and therefore the EnsurePermissions-annotated method will NOT get executed.

For our example, this is what it will end up looking like:

```kotlin  
class FinishActivityOnDeniedStrategy : ActivityRequestStrategy() {  
  
    override fun onPermissionsRequestResults(  
        host: Activity,  
        data: PermissionsResult  
    ): Boolean {  
        if (data.denied.isNotEmpty()) {  
            // At least one permission was denied.  
            host.finish()  
            return false // So that the annotated method doesn't get called.  
        }  
  
        // No permissions were denied, therefore proceed to call the annotated method.  
        return true  
    }  
  
    override fun getName(): String {  
        // We can return anything here, as long as there is no other RequestStrategy with the same name.  
        return "FinishActivityOnDenied"  
    }  
}  
```  

As we can see in `onPermissionsRequestResults`, we check the `denied` permissions list we get
from `data`, and we verify if it is not empty, which would mean that there are some denied
permissions, therefore our Strategy will treat the request process as failed and will return `false`
so that the annotated method won't get called, and before that, we call `host.finish()`, in order to
close our Activity too.

If the `denied` permissions list is empty, it means that all of the requested permissions were
approved, therefore our Strategy will treat the request process as successful and will return `true`
in order to proceed to call the annotated method.

#### Other configurable aspects of a RequestStrategy

You can customize other things in your custom `RequestStrategy`, such as the `requestCode` of the
permission's request for example, by overriding the `getRequestCode()` method. You can also change
the behavior of the pre-request action, for example if you want to display some information before
requesting for some permissions, you can do so as well. More info on this, below
under `Changing the pre-request behavior`.

Finally, you can even change things such as how to launch a System's permission dialog request, and
also how to change the way your Strategy queries the current granted permissions of your app, by
overriding the respective `RequestStrategy` getters. More info on this, below
under `Advanced configuration`.

### Using our custom Strategy

#### Registering it

In order to use our new `FinishActivityOnDeniedStrategy` request strategy, we must first register it
right after Aaper's initialization:

```kotlin  
class MyApp : Application() {  
  
    override fun onCreate() {  
        super.onCreate()  
        Aaper.init()  
        val strategyProvider = Aaper.getRequestStrategyProvider() as DefaultRequestStrategyProvider  
        strategyProvider.register(FinishActivityOnDeniedStrategy())  
    }  
}  
```  

We can register as many Strategies as we like, as long as they all have unique names. After
registering our new `RequestStrategy`, we can either:

#### Use it per annotation param only

This can be achieved by passing our strategy's name into the `EnsurePermissions` annotation of a
method, like so:

```kotlin  
@EnsurePermissions(  
    permissions = [(PERMISSION NAMES)],  
    strategyName = "FinishActivityOnDenied" // The name that we return in our custom strategy's `getName()` method.  
)  
fun methodThatNeedsThePermissions() {...}  
```  

#### Or, Set it as the default strategy

We can set our custom RequestStrategy as default for all the annotated methods by doing the
following after registering our custom Strategy:

```kotlin  
// Application.onCreate  
// ...  
strategyProvider.register(FinishActivityOnDeniedStrategy())  
strategyProvider.setDefaultStrategyName("FinishActivityOnDenied") // The name that we return in our custom strategy's `getName()` method.  
```  

After doing so, you won't have to explicitly pass "FinishActivityOnDenied" to
the `EnsurePermissions` annotation in order to use this custom strategy, as it will be the default
one.

### Changing the pre-request behavior

Sometimes we want to do something right before launching our permissions request, such as displaying
an information message that explains the users why our app needs the permissions that it is about to
request.

In order to make our custom `RequestStrategy` able to handle those cases, we can override the
method `onBeforeLaunchingRequest`, which is called right before launching the System's permissions
request dialog. Following our previous example, if we override such method, our custom strategy will
look like the following:

```kotlin  
class FinishActivityOnDeniedStrategy : ActivityRequestStrategy() {  
  
    // Other methods...  
  
    override fun onBeforeLaunchingRequest(  
        host: Activity,  
        data: PermissionsRequest,  
        request: RequestRunner  
    ): Boolean {  
        return super.onBeforeLaunchingRequest(host, data, request)  
    }  
}  
```  

The `onBeforeLaunchingRequest` method returns a `boolean` which by default is `FALSE`.

- When `onBeforeLaunchingRequest` returns FALSE, it allows Aaper to proceed to launch the System's
  permissions request dialog.
- When `onBeforeLaunchingRequest` returns TRUE, Aaper won't launch the System's permission request
  dialog, and rather **it'll have to be run manually** by the `RequestStrategy` at some point, this
  is achieved by calling the `RequestRunner.run()` method of the `request` parameter passed
  to `onBeforeLaunchingRequest`.

The `onBeforeLaunchingRequest` method provides us with three parameters, host, data (contains the
permissions requested for the annotated method) and, the most important one, the `RequestRunner`.

The `RequestRunner` is a runnable object that, when is run, it launches the System's permission
request dialog. This method should only be called if the `onBeforeLaunchingRequest` method
returns `TRUE`, which means that the Strategy will do some operation prior to the permission
request. When the pre-request process is done and the `RequestStrategy` wants to proceed launching
the System's permission dialog, it then must call `RequestRunner.run()`.

#### Example

In this pretty simple example, we use a dialog with a single button, if the user clicks on it, then
we launch the permissions request, otherwise we don't.

```kotlin  
// My custom RequestStrategy  
  
// ...  
override fun onBeforeLaunchingRequest(  
        host: Activity,  
        data: PermissionsRequest,  
        request: RequestRunner  
    ): Boolean {  
        val infoDialog = AlertDialog.Builder(host).setPositiveButton("CONTINUE") { _, _ ->  
            // When the user has read the information and wants to continue.  
            request.run() // Execute the runnable to launch the System's permission dialog.  
        }.setTitle("We need these permissions")  
            .setMessage("Pretty please approve the permissions :(")  
            .create()  
  
        infoDialog.show()  
  
        return true // This is so that Aaper doesn't launch the permissions request as we're going to launch it manually.  
    }  
```  

Adding Aaper into your project
---  

### Prerequisites

#### Android Gradle plugin >= 7.2

Aaper relies on
an [API](https://developer.android.com/reference/tools/gradle-api/7.2/com/android/build/api/variant/Instrumentation)
added in the Android Gradle plugin version 7.2 that allows to modify bytecode at compile time
using [ASM](https://asm.ow2.io/), this allows Aaper to write all the boilerplate code for you,
therefore it will be required for your project to use at least version `7.2.0` of the Android Gradle
plugin or higher.

### Aaper Gradle dependency

In order to add the [Aaper plugin](https://plugins.gradle.org/plugin/com.likethesalad.aaper) into
your project, you just have to add the following line into your app's build.gradle `plugins` block:

```groovy  
id 'com.likethesalad.aaper' version '2.0.0'
```

**Full app's build.gradle example:**

```groovy  
// Your app's build.gradle file  
plugins {
    id 'com.android.application'
    id 'com.likethesalad.aaper' version '2.0.0'
}
```  

Troubleshooting
---  

### I get a NPE when calling my annotated method at runtime

Make sure you've called `Aaper.init()` within your Application class. If you don't initialize Aaper
it will throw a `java.lang.NullPointerException` when executed.

### The OS permission dialog doesn't show up

Make sure that the permissions you've added to the `EnsurePermissions` annotation are ALSO added to
your `AndroidManifest.xml` file. The Android OS will ignore any permission request for permissions
not listed within your app's manifest.

Advanced configuration
---  

### Creating a custom RequestStrategyProvider

Aaper's behavior is all about its `RequestStrategy` objects, and the way Aaper can access to them,
is through an instance of `RequestStrategyProvider`. By default, when you initialize Aaper like
so: `Aaper.init()`, the `RequestStrategyProvider` that Aaper will use in that case, would
be `DefaultRequestStrategyProvider`.

The `DefaultRequestStrategyProvider` implementation contains a map of `RequestStrategy` instances to
which Aaper can access later on by providing the name of the Strategy it needs, such default
provider can let you register your own Strategies and also override the default's Strategy name, as
we saw above under `Using our custom Strategy`, so in essence, the `DefaultRequestStrategyProvider`
should suffice for most cases.

Sometimes, providing instances of custom `RequestStrategy` classes as part of Aaper's initialization
might not be possible, or just not good for your App's performance (in the case that there's a lot
of custom Strategies). For these cases, you can create your own implementation
of `RequestStrategyProvider`, where you'd be able to provide your own `RequestStrategy` instances
the way you'd like the most, either by creating them on-demand or just by storing them in memory, or
both. Implementing from `RequestStrategyProvider` is pretty straightforward as it only requires you
to override two methods:

```kotlin  
class MyRequestStrategyProvider : RequestStrategyProvider() {  
  
    override fun getStrategyForName(host: Any, name: String): RequestStrategy<out Any> {  
        TODO("Not yet implemented")  
  }  
  
    override fun getDefaultStrategy(host: Any): RequestStrategy<out Any> {  
        TODO("Not yet implemented")  
  }  
}  
```  

As you can see, both methods are related to providing a `RequestStrategy` instance, one for the
default one, and the other for every other case. You can take a look at the javadoc for more details
on the class and its methods: https://javadoc.io/doc/com.likethesalad.android/aaper-api.

#### Using your custom RequestStrategyProvider

After you've created your own `RequestStrategyProvider`, you can set it into Aaper's initialization
method like so:

```kotlin  
class MyApp : Application() {  
  
    override fun onCreate() {  
        super.onCreate()  
        val myRequestStrategyProvider = MyRequestStrategyProvider()  
        Aaper.init(myRequestStrategyProvider)  
    }  
}  
```  

And that's it, Aaper will now use your custom `RequestStrategyProvider` in order to get all of the
Strategies it needs.

### Overriding permission's status query and request launch

There are two methods in every `RequestStrategy` that provide the tools to both query if a
permission is granted and also to launch a set of permissions' request, those methods
are `getPermissionStatusProvider`, which provides an instance of `PermissionStatusProvider`,
and `getRequestLauncher`, which provides an instance of `RequestLauncher`. More info on these
classes in the javadoc: https://javadoc.io/doc/com.likethesalad.android/aaper-api.

For the `PermissionStatusProvider` class, the defaults for both `Activity` and `Fragment` is to
use `androidx.core.content.ContextCompat.checkSelfPermission`, and for the `RequestLauncher` one,
the Activity's implementation makes use of `ActivityCompat.requestPermissions`, whereas for
Fragment's implementation, the `requestPermissions` method is called straight from the host Fragment
itself.

The defaults for both Activity and Fragment operations should suffice for all cases, though if for
whatever reason you'd like to customize these actions, you can just override the aforementioned
getters in your custom `RequestStrategy` and provide your own implementations for these classes.

License
---  

    MIT License  
  
    Copyright (c) 2020 LikeTheSalad.  
  
    Permission is hereby granted, free of charge, to any person obtaining a copy  
    of this software and associated documentation files (the "Software"), to deal  
    in the Software without restriction, including without limitation the rights  
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
    copies of the Software, and to permit persons to whom the Software is  
    furnished to do so, subject to the following conditions:  
  
    The above copyright notice and this permission notice shall be included in all  
    copies or substantial portions of the Software.  
  
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  
    SOFTWARE.

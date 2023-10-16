# SkipUnit: Skip Unit Testing Support

Unit testing for [Skip](https://skip.tools) apps, adapting Swift XCUnit to transpiled Kotlin JUnit test cases.

## About

SkipUnit vends the `skip.unit` Kotlin package containing a Swift `XCTest` interface to the Java/Kotlin `JUnit` testing framework.
This provides automatic transpilation and testing of XCUnit test cases from Swift to Java,
which enables parity testing to identify and isolate any differences between the Swift code and the transpiled Kotlin code.

## Dependencies

SkipUnit depends on the [skip](https://source.skip.tools/skip) transpiler plugin and has no additional library dependencies.

It is part of the core *skipstack* and is not intended to be imported directly.
The module is transparently adopted through the translation of `import XCUnit` into `import skip.unit.*` by the Skip transpiler.

## Parity Testing

Parity testing is a central aspect of Skip development. It ensures that your Swift and Kotlin behave exactly the same.

You do not need to import any Skip-specific libraries or APIs in order to perform parity testing. 
The standard modules of `XCTest` and `Foundation`, as well as your own library dependencies, will be sufficient for performing most test operations.
Your tests **do** need to run against both the macOS and iOS, because the Skip tests are only executed when running on the macOS destination.

## Testing Modes

When writing Swift code that doesn't need access to device-specific services or other iOS-specific capabilities, the fastest development path is to build and run on macOS against the Cocoa APIs. Most common APIs behave the same between macOS and iOS, and running tests locally against the Cocoa version of an app enables quick unit testing cycles, both when developing with Xcode interactively, as well as running as part of a continuous integration (CI) process by running `swift test`. When tests need iOS-specific functionality, those tests can be gated inside `#if os(iOS)` blocks on order to enable them to be run only when targeting the iOS Simulator or device.

Similarly, when running transpiled Kotlin/Gradle tests, the fastest development mode is to run the JUnit tests locally without launching an Android emulator or connecting to a device. While the Java and Kotlin APIs that underly `SkipFoundation` are sufficient for many testing needs, there are some Android-specific APIs that are needed. This compatibility is provided by the `Robolectric` framework, which provides a set of Android-compatible APIs for app testing. 

These six modes of testing can be summarized by the following table:

|          | Swift              | Kotlin           | Fidelity | Speed |
| ---------|--------------------|------------------|----------|-------|
| Local    | macOS w/ Cocoa API | macOS w/ Robolectric API | lowest | highest |
| Simula   | iOS Simualtor      | Android Emulator | good | slower |
| Actual   | iOS Device         | Android Device   | highest | slowest |

### Writing tests

A standard Skip test looks something like this:

```swift
import XCTest

final class MyUnitTests: XCTestCase {
    func testSomeLogic() throws {
        XCTAssertEqual(1 + 2, 3, "basic test")
    }
}
```

### Adding Tests to a project

The transpiled unit tests are intended to be run as part of the standard Xcode and Swift Package Manager testing process.

This is done by adding one additional test class to the project's `Tests/ModuleNameTests/` folder named `XCSkipTests.swift`.

This additional test class is added automatically when a library is created with the `skip init` command. 
When adopting Skip into an existing process, add the test case manually:

```
#if os(macOS) // Skip transpiled tests only run on macOS targets
import SkipTest

/// This test case will run the transpiled tests for the Skip module.
@available(macOS 13, *)
final class XCSkipTests: XCTestCase, XCGradleHarness {
    public func testSkipModule() async throws {
        try await runGradleTests(device: .none) // set device ID to run in Android emulator vs. robolectric
    }   
}       
#endif
```


### Running Tests from Xcode

Once the `XCSkipTests.swift` file has been added to a project, the transpiled test cases will automatically run whenever testing against the macOS run destination.
As such, you need to ensure that your Swift code compiles and runs the same on macOS and iOS.
This is a pre-requisite for Skip's parity testing, which runs the XCUnit test cases on macOS against the transpiled Kotlin tests in the Android testing environment.

The transpiled unit tests are run by forking the `gradle test` process on the macOS host machine against the output folder of the skip transpiler plugin.
The JUnit test output XML files are then parsed, and a report summarizing the test results is presented to the user.

### Running Tests from the CLI/CI

The `swift test` command on macOS will automatically perform test transpliation.
This can be used for headless testing locally as well as on a continuous integration (CI) server.

Note that running test cases will also initiate a gradle build, which has the side-effect of gradle downloading all the library dependencies for the modules. When tests depend on frameworks like `SkipUI`, which depends on may Jetpack Compose libraries, the dependencies can amount to over 1 gigabyte in the `~/.gradle/` folder. 

This may lead to a slow initial run of the tests and a perception that the tests may be hanging or excessively slow.
Subsequent runs will use the cached dependencies, and will thus run much more quickly.

### Test Failures

Test failures differ in the XCTest and JUnit worlds. 

When an `XCTAssert*` failure occurs in Swift, the test failure is noted, but the test continues to run.
When the adapted `assert*` failure occurs in Kotlin, that failure is signalled by an exception, which halts the execution of that test method.
This distinction can be noted in the differing number of test failures that occur when mulitple `XCTAssert*` failures occur.
The same applies to `XCTFail`, but not to `XCTSkip`, which is the supported and recommended way to prevent tests from running in one or the other environment.

## Implementation Notes

The adaptation from Swift XCUnit to Kotlin JUnit test cases is quite simple.
For example:

```kotlin
fun XCTAssertEqual(a: Any?, b: Any?): Unit = org.junit.Assert.assertEquals(b, a)
```

### Transpiled Kotlin Test Case

While you may never need to intereact with it directly,
the transpiled Kotlin for the example test case above looks like this:

```kotlin
package app.module.name

import skip.lib.*

import skip.unit.*

internal class MyUnitTests: XCTestCase {
    @Test
    internal fun testSomeLogic() = XCTAssertEqual(1 + 2, 3, "basic test")
}
```


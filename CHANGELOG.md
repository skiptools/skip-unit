## 0.8.0

Released 2024-07-03

  - Update to Kotlin 2 and bump other dependencies; update skip-build plugin to raise errors about required updates needed to existing projects

## 0.7.5

Released 2024-06-21

  - Change target JVM version from 21 to 17 for Android Studio compatibility
  - Add jvmTarget to kotlinOptions to harmonize with sourceCompatibility and targetCompatibility
  - Update README.md

## 0.7.4

Released 2024-06-10

  - Remove sourceCompatibility and targetCompatibility

## 0.7.3

Released 2024-06-10

  - Remove forced requirement for jdk 21 jvmToolchain

## 0.7.2

Released 2024-06-02

  - Handle case where the output folder name does not match the Skip project name and provide a descriptive error

## 0.7.1

Released 2024-06-02

  - Warn when launching on multiple emulator/device instances
  - Update gradle plugin to launch app on each connected Android device/emulator, or raise a descriptive error if there are no connected devices

## 0.7.1

Released 2024-06-01

  - Update gradle plugin to launch app on each connected Android device/emulator, or raise a descriptive error if there are no connected devices

## 0.7.0

Released 2024-05-29

  - Bump kotlin-coroutines to 1.8.1

## 0.6.6

Released 2024-05-29

  - Bump kotlin-coroutines to 1.8.1

## 0.6.5

Released 2024-04-01

  - Fix versionCode generation to convert string to integer

## 0.6.4

Released 2024-03-30

  - Replace dash with underscore to convert PRODUCT_BUNDLE_IDENTIFIER into valid Android applicationId
  - ci: update workflow actions location

## 0.6.2

Released 2024-03-24

  - Add androidx.test:core and androidx.test:rules to dependencies
  - Update README with note about testing internal API

## 0.6.1

Released 2024-02-18


## 0.6.0

Released 2024-02-15

  - Bump JDK version from 17 to 21

## 0.5.4

Released 2024-02-15

  - Bump JDK version from 17 to 21

## 0.5.3

Released 2024-02-14

  - Gradle actions for publishing framework artifacts

## 0.5.2

Released 2024-02-11

  - Add buildSrc for Skip gradle plugin

## 0.5.1

Released 2024-02-10

  - Re-enable suppressWarnings for KotlinCompile tasks

## 0.5.0

Released 2024-02-05

  - Use gradle version catalogs for dependencies

## 0.4.8

Released 2024-01-12


## 0.4.7

Released 2023-12-11

  - Add CHANGELOG.md

## 0.4.6

Released 2023-11-27

  - Add maven.skip.tools as a repository

## 0.4.5

Released 2023-11-21

  - Swift from RepositoriesMode.FAIL_ON_PROJECT_REPOS to PREFER_PROJECT
  - Update README

## 0.4.3

Released 2023-10-27

  - Revert to depending on JDK 17

## 0.4.2

Released 2023-10-27

  - Specify jvmToolchain/jvmTarget 20
  - Remove jvmTarget and jvmToolchain
  - Change jvmToolchain from 17 to 20

## 0.4.1

Released 2023-10-24

  - Update README.md
  - Update README
  - Update README

## 0.4.0

Released 2023-10-23

  - Update README

## 0.3.9

Released 2023-10-06

  - Export dependencies in build.gradle.kts as api

## 0.3.5

Released 2023-09-29

  - Update README.md for consistency between modules
  - Docs

## 0.3.4

Released 2023-09-24

  - Update README with some docs

## 0.3.3

Released 2023-09-18

  - Remove SKIP INSERT from annotations in .kt file

## 0.3.2

Released 2023-09-13

  - Remove exported import of XCTest

## 0.2.24

Released 2023-09-11

  - Remove Robolectric from androidTestImplementation

## 0.2.20

Released 2023-09-08

  - Add all test dependencies as androidTestImplementation as well

## 0.2.15

Released 2023-09-06

  - Move XCTest utilities over to skip

## 0.2.6

Released 2023-09-05

  - Refactor Skip plugin

## 0.0.18

Released 2023-08-21

  - Update dependenices


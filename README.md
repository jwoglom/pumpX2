
<a href="https://github.com/jwoglom/pumpX2/actions/workflows/android.yml">
<img src="https://github.com/jwoglom/pumpx2/actions/workflows/android.yml/badge.svg" />
</a>


# PumpX2

Reverse-engineered Bluetooth protocol for the Tandem t:slim X2.

Currently functions as a basic Android application which pairs to the pump
and allows sending read-only message requests and receiving responses about
the pump's status and history. An Android library is exported which can be used
by other projects to interface with the pump, as well as a generic Java library for
parsing messages which has no Android-specific dependencies.

[View supported request/response/history log messages][sheet]

[Discuss in #tslim-x2-dev on Discord][discord]

**Currently supported:**

* Bluetooth connection
* Pump authentication and pairing
* currentStatus characteristic request messages

**Partially supported:**

* History Log parsing

**Not supported:**

* "V2" pump request/response messages (self-reported API version >= 2.2)
* Control features (remote bolus support is in progress)

[sheet]: https://docs.google.com/spreadsheets/d/e/2PACX-1vTDnXBbJfiwVh-5PDK78RZqgI7C7ymOl-aEw5JLCV8rl7AiYZdoTwx_gBkWUZoducIxh7JXlOJJd9p6/pubhtml?gid=1917691101&single=true
[discord]: https://discord.gg/4fQUWHZ4Mw


## Setup

* Download the most recent debug APK from the Actions tab in GitHub
* `adb install` to your device
* Disconnect from any tslim Bluetooth devices on your phone
* If you have the official t:connect app, **force quit or log out** to avoid conflicts between the two applications. After pairing with pumpX2, you will need to log out and re-pair with the official Tandem app.
* Enable the Mobile Connection in Bluetooth Settings on the pump, and Unpair Device if currently enabled
* Open the app and accept the Bluetooth pairing
* Enter the pairing code from Pair Device

The current implementation of the Bluetooth pairing code is not especially stable and is primarily a proof-of-concept.
You may need to re-open the app, clear app data, and/or unpair and re-pair the devices to get things working.


<img src="https://user-images.githubusercontent.com/192620/176375500-29a0d093-18bc-4cf6-b4d9-6ab1dd004d43.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375589-578c6bb6-e993-4087-8f06-0d38d6edf989.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375806-85950622-b6d9-44e5-8374-8f45c9268fa5.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375930-1750cae6-0d31-4104-8c1b-f9a67d711c59.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375889-577f30cf-e39a-4251-a2de-5b8581eb4650.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375967-eaea7b4a-e265-4d81-83b6-9cbd93d0fbcf.png" width="300" />

## Build Instructions

To build the project, generating AAR (Android library) and JAR (Java library/executable) files:
```
./gradlew build
./gradlew publishToMavenLocal
```

The generated files will be created in `~/.m2/repository/com/jwoglom/pumpx2/`.
You can alternatively download the most recent versions from the "Android CI" Github Action.

To integrate the PumpX2 Android library into your project, import the following dependencies into your project:

* `pumpx2-androidLib.aar`, the Android Bluetooth library
* `pumpx2-messages.jar`, the common message-parsing code.
* `pumpx2-shared.jar`, a common shared module.

If Android-specific Bluetooth code is not needed, then `messages` and `shared` can be used in a standard, non-Android Java project.

Other Gradle dependencies are currently required by PumpX2 but not included in the JAR/AAR.
For an Android project:

```
dependencies {
    implementation files('androidLib-1.1.0.arr');
    implementation files('messages-1.1.0.jar');
    implementation files('shared-1.1.0.jar');

    implementation "commons-codec:commons-codec:1.15"
    implementation "org.apache.commons:commons-lang3:3.12.0"
    implementation "com.google.guava:guava:31.0.1-android"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
}
```

For a non-Android Java project:

```
dependencies {
    implementation files('messages-1.1.0.jar');
    implementation files('shared-1.1.0.jar');

    implementation "commons-codec:commons-codec:1.15"
    implementation "org.apache.commons:commons-lang3:3.12.0"
    implementation "com.google.guava:guava:31.0.1-android"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
}
```

A `messages-all.jar` is also available on Github Actions as a fat JAR which contains all dependencies.
This may or may not work with your project depending on if there are any conflicting dependencies.

To utilize the command-line message-parsing tool, execute the `cliparser-all.jar` file using `java -jar`.
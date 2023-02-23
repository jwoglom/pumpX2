
<a href="https://github.com/jwoglom/pumpX2/actions/workflows/android.yml">
<img src="https://github.com/jwoglom/pumpx2/actions/workflows/android.yml/badge.svg" />
</a>

# PumpX2

API library with a reverse-engineered Bluetooth protocol for the Tandem t:slim X2.

### :warning: Are you looking to control your pump from your phone and not a developer? You are in the wrong place.

[For an application to connect to your pump, see ControlX2.](https://github.com/jwoglom/controlX2)

---

PumpX2 is an Android library is exported which can be used by other projects to interface with the pump,
as well as a generic Java library for parsing messages which has no Android-specific dependencies.
**This project is experimental and still under development.**

[Discuss in #tslim-x2-dev on Discord][discord]

**Currently supported:**

* Bluetooth connection
* Pump authentication and pairing
* Read-only view of the pump's current status (currentStatus characteristic)
* Pump event notifications: alerts, alarms, etc. (QualifyingEvent)
* **Remote bolus** (insulin unit amounts only; carbs / auto-corrections not yet supported)

**Partially supported:**

* History Log parsing (testing still needed to decode all messages)

**Not supported:**

* Non-bolus control features: these are not supported in current t:slim X2 firmware
* Bolus wizard with automatic unit calculation from carbs / BG

[discord]: https://discord.gg/4fQUWHZ4Mw


## Setup (for developers)

**This process should only be followed for developers who want to experiment with the API that PumpX2 provides.**
If you're not a developer, [see ControlX2.](https://github.com/jwoglom/controlX2)

* Download the most recent debug APK from the Actions tab in GitHub of the sample application.
* `adb install` to your device
* Disconnect from any tslim Bluetooth devices on your phone
* If you have the official t:connect app, **force quit or log out** to avoid conflicts between the two applications. After pairing with pumpX2, you will need to log out and re-pair with the official Tandem app.
* Enable the Mobile Connection in Bluetooth Settings on the pump, and Unpair Device if currently enabled
* Open the app and accept the Bluetooth pairing
* Enter the pairing code from Pair Device

The current implementation of the Bluetooth pairing code is not especially stable and is primarily a proof-of-concept.
You may need to re-open the app, clear app data, and/or unpair and re-pair the devices to get things working.


<img src="https://user-images.githubusercontent.com/192620/176375500-29a0d093-18bc-4cf6-b4d9-6ab1dd004d43.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375589-578c6bb6-e993-4087-8f06-0d38d6edf989.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375806-85950622-b6d9-44e5-8374-8f45c9268fa5.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375930-1750cae6-0d31-4104-8c1b-f9a67d711c59.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375889-577f30cf-e39a-4251-a2de-5b8581eb4650.png" width="300" /><img src="https://user-images.githubusercontent.com/192620/176375967-eaea7b4a-e265-4d81-83b6-9cbd93d0fbcf.png" width="300" />

### Maven Packages

**For an Android project,** load the following Maven packages [from JitPack](https://jitpack.io):

* `com.github.jwoglom.pumpX2:pumpx2-android`
* `com.github.jwoglom.pumpX2:pumpx2-messages`
* `com.github.jwoglom.pumpX2:pumpx2-shared`

For example, using Gradle, first add the JitPack repositories to your root project `settings.gradle`:
```
pluginManagement {
    repositories {
        maven {
            url 'https://jitpack.io'
        }
    }
}
```

Add a reference to the PumpX2 version in your root project `build.gradle`:
```
buildscript {
    ext {
        pumpx2_version = "x.x.x"
    }
}
```

And then reference these dependencies in your module's `build.gradle`:
```
dependencies {
    [...]
    implementation "com.github.jwoglom.pumpX2:pumpx2-android:v${project.pumpx2_version}"
    implementation "com.github.jwoglom.pumpX2:pumpx2-messages:v${project.pumpx2_version}"
    implementation "com.github.jwoglom.pumpX2:pumpx2-shared:v${project.pumpx2_version}"
}
```

Then, to use PumpX2, subclass [the `com.jwoglom.pumpx2.pump.bluetooth.TandemPump` class](https://github.com/jwoglom/pumpX2/blob/main/androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java),
and initialize the library with:

```
TandemPump tandemPump = new SubclassedTandemPump(getApplicationContext());
TandemBluetoothHandler bluetoothHandler = TandemBluetoothHandler.getInstance(getApplicationContext(), tandemPump);
```

**For a Java (non-Android) project,** load the following Maven packages:

* `com.github.jwoglom.pumpX2:pumpx2-messages`
* `com.github.jwoglom.pumpX2:pumpx2-shared`

Look at [the `cliparser` Gradle module](https://github.com/jwoglom/pumpX2/blob/main/cliparser/src/main/java/com/jwoglom/pumpx2/cliparser/Main.java)
for an example of how to use the library.


### Build Instructions

To build the project, generating AAR (Android library) and JAR (Java library/executable) files:
```
./gradlew build
./gradlew publishToMavenLocal
```

The generated files will be created in `~/.m2/repository/com/jwoglom/pumpx2/`.
You can alternatively download from Github Packages, or use the most recent versions from the "Android CI" Github Action. 

To integrate the PumpX2 Android library into your project, import the following dependencies into your project:

* `pumpx2-android.aar`, the Android Bluetooth library
* `pumpx2-messages.jar`, the common message-parsing code.
* `pumpx2-shared.jar`, a common shared module.

If Android-specific Bluetooth code is not needed, then `messages` and `shared` can be used in a standard, non-Android Java project.

Other Gradle dependencies are currently required by PumpX2 but not included in the JAR/AAR.
For an Android project:

```
dependencies {
    implementation files('pumpx2-android-1.1.0.arr');
    implementation files('pumpx2-messages-1.1.0.jar');
    implementation files('pumpx2-shared-1.1.0.jar');

    implementation "commons-codec:commons-codec:1.15"
    implementation "org.apache.commons:commons-lang3:3.12.0"
    implementation "com.google.guava:guava:31.0.1-android"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
}
```

For a non-Android Java project:

```
dependencies {
    implementation files('pumpx2-messages-1.1.0.jar');
    implementation files('pumpx2-shared-1.1.0.jar');

    implementation "commons-codec:commons-codec:1.15"
    implementation "org.apache.commons:commons-lang3:3.12.0"
    implementation "com.google.guava:guava:31.0.1-android"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.7.10"
}
```

A `pumpx2-messages-all.jar` is also available on Github Actions as a fat JAR which contains all dependencies.
This may or may not work with your project depending on if there are any conflicting dependencies.

To utilize the command-line message-parsing tool, execute the `pumpx2-cliparser-all.jar` file using `java -jar`.

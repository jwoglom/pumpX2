# Bluetooth btsnoop logging on iOS

## iOS Setup

[More detailed instructions here](https://www.bluetooth.com/blog/a-new-way-to-debug-iosbluetooth-applications/)

1. On your iOS device, go to https://developer.apple.com/bug-reporting/profiles-and-logs/ and select iOS > Bluetooth
2. Download and open the `iOSBluetoothLogging.mobileconfig` file.
3. Open Settings, click on the profile installation option at the top of the settings list ("Profile Downloaded"), and install the profile.
   If the option does not appear, close and re-open the Settings app.

## MacOS Setup
1. On a Mac, [download and install the latest stable version of Xcode](https://developer.apple.com/download/more/?=xcode) if not already installed.
2. On a Mac, [download the version of **Additional Tools for Xcode** which matches your Xcode installation](https://developer.apple.com/download/all/?q=xcode).
   This separate download is required even if you already have Xcode installed.
3. Inside the Additional Tools for Xcode dmg, find Hardware > PacketLogger and copy it to your Applications folder.

## Generating a log
### Option 1. Logging via PacketLogger
NOTE: This has worked for me on previous MacOS/iOS versions, but does not work for me on Sequoia.

1. Configure your iOS device in a state where it is ready to begin collecting packets. For example, disconnect your pump from the t:connect or Mobi app.
2. Open PacketLogger on your Mac, and plug your iOS device into your Mac via USB cable.
3. In PacketLogger, select File > New iOS Trace. The name of your iPhone should appear in the title bar.

While using PacketLogger, if you see the message "iOS Device connection has been lost", you should reconnect your iPhone and create a new trace via File > New iOS Trace.
You should also ensure your Mac and iPhone are on the latest iOS versions.

If you do not see any returned packets in the PacketLogger UI, then something isn't working. Restart your Mac and iPhone and try again.

Make sure the version of the additional tools package matches Xcode's version. If the versions do not match, PacketLogger may silently not work without any reported errors.

4. Perform the steps you would like to test. When pairing a new pump, **write down the pairing code so you can reference it later.**
5. In PacketLogger, hit File > Export > BTSnoop.


### Option 2. Logging via sysdiagnose
[More detailed instructions here](https://support.umbrella.com/hc/en-us/articles/4406646902420-How-to-capture-a-sys-diagnose-from-an-iOS-device)

1. Ensure you've restarted your device since enabling the Bluetooth logging profile.
2. Go to Settings > Developer > Bluetooth Logging and turn HID Logging On.
3. Turn Bluetooth off and then on again.
4. Configure Assistive Touch to trigger a sysdiagnose. Go to Settings > Accessibility > Touch, and turn on Assistive Touch.
5. Tap Customize Top Level Menu, choose one of the slots, and select "Analytics". You can now click on the assistive touch menu and tap Analytics to trigger a sysdiagnose.
7. Perform the steps you would like to test. When pairing a new pump, **write down the pairing code so you can reference it later.**
8. Trigger a sysdiagnose by clicking the assistive touch menu and tap Analytics.
9. Wait anywhere from 5-10 minutes.
10. Open Settings > Privacy & Security > Analytics & Improvements > Analytics Data
11. Look for the item beginning with `sysdiagnose_` and the current timestamp. You can filter to `sysdiagnose` in the search bar at the top. **This can take 10 minutes or longer to appear until the sysdiagnose completes.**
12. Hit the share icon and AirDrop the file to your Mac.
13. Extract the `sysdiagnose_XXXX.tar.gz` file, and look inside the `logs/Bluetooth` folder for a `bluetoothd-hci-xxx.pklg` file.
14. Open the pklg file in PacketLogger, and hit File > Export > BTSnoop.


## PumpX2 parsing

1. From the PumpX2 repo root, run:
```bash
PUMP_AUTHENTICATION_KEY=123456 scripts/process-btsnoop-hci.sh /path/to/bluetoothd-hci-2024-11-20_15-24-02.log
```

where `123456` is your 6-character pump pairing code, and the `.log` file is the exported BTSnoop from the prior step.

After it runs, a `.csv` and `.jsonl` file will be placed in the same directory as the log file.
The `.csv` is a pre-processed extraction of the raw Bluetooth characteristic and values, while the `.jsonl` is parsed via PumpX2 using the cliparser module.

2. Look at the `.jsonl` file using tools like jq that can operate on jsonlines (one JSON element per line of the file), e.g.:

```bash
cat /path/to/bluetoothd-hci-2024-11-20_15-24-02.jsonl | jq -c -s '.[] | select(.parsed.name) | [.raw.type, .parsed.messageProps.characteristic, .parsed.name, .raw.value, .parsed.params]'
```

You can generate a CSV containing parsed data via:

```bash
cat /path/to/bluetoothd-hci-2024-11-20_15-24-02.jsonl | jq -c -s '[.[] | select(.parsed.name) | [.raw.type, .parsed.messageProps.characteristic, .parsed.name, .raw.value, .parsed.params|tostring]]' | jq -r 'map(. | @csv) | join("\n")'
```

Note that these exclude unprocessable bluetooth messages -- see json lines containing an `error` key.
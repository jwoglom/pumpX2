
If rooted:
```
adb shell
su
cp /data/misc/bluetooth/logs/btsnoop_hci.log /sdcard/btsnoop_hci_xxx.log
adb pull /sdcard/btsnoop_hci_xxx.log ~/btsnoops/
./scripts/process-btsnoop-hci.sh ~/btsnoops/btsnoop_hci_xxx.log
```

If not rooted:
```
filename=$(date +"%Y-%m-%dT%H-%M-%S%z")
adb bugreport $filename
./scripts/process-android-bugreport.sh ${filename}.zip
```

---

Older, manual steps:
```
tshark -r ~/btsnoops/btsnoop_hci_1u_override_bolus.log -T fields -E separator=, -E quote=d -e frame.number -e btatt.opcode -e btatt.value 'btatt.value'
./scripts/get-btsnoop-opcodes.py ~/btsnoops/btsnoop_unavailable_deliveries_stopped.csv > ~/btsnoops/btsnoop_unavailable_deliveries_stopped.tsv
```
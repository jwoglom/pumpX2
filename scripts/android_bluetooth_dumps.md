adb shell
su
cp /data/misc/bluetooth/logs/btsnoop_hci.log /sdcard/btsnoop_hci.log
adb pull /sdcard/btsnoop_hci.log ~/btsnoops/

Open the .log file in Wireshark
Filter to `btatt.opcode == 0x1b || btatt.opcode == 0x52`

cmd+A
File > Export packet dissections > to CSV, Selected packets only

./scripts/get-btsnoop-opcodes.py ~/btsnoops/btsnoop_unavailable_deliveries_stopped.csv > ~/btsnoops/btsnoop_unavailable_deliveries_stopped.tsv
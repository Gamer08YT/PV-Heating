sudo raspi-config nonint do_onewire 0
sudo reboot

# Debug:

### List Devices:
ls -la /sys/bus/w1/devices/

### Get Value Example:
cat /sys/bus/w1/devices/28-15ff791f64ff/w1_slave

Returns:
```
47 01 55 00 7f ff 0c 10 51 : crc=51 YES
47 01 55 00 7f ff 0c 10 51 t=20437
```
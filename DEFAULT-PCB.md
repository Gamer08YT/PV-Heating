# Default PCB
___

### Pinout:

<img height="500px" src="frontend/themes/PV-Heating/media/raspberry-pi-pinout.png"/>

| Pin    | Type         | Description        |
|--------|--------------|--------------------|
| GPIO04 | INPUT/WIRE 1 | X2.1 (Temperature) |
| GPIO05 | OUT/PWM      | Led 2 (P2)         |
| GPIO06 | INPUT        | X2.3 (Flow Sensor) |
| GPIO12 | OUT/PWM      | Led 1 (P1)         |
| GPIO13 | OUT/PWM      | SCR In             |
| GPIO17 | INPUT        | SCR Betrieb        |
| GPIO22 | INPUT        | Button 1 (S2)      |
| GPIO26 | INPUT        | Button 2 (S3)      |
| GPIO27 | INPUT        | SCR Störung        |

```mermaid
graph TD
    Raspberry <--> Modbus <--> Meter
    Raspberry <--> GPIO <--> Wire[Wire 1] <--> Temp1[Temperature 1]
    Raspberry <--> HomeAssistant
    Wire <--> Temp2[Temperatur 2]
    GPIO <--> Flow[Flow Meter]

```
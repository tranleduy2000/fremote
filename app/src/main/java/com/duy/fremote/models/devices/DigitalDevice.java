package com.duy.fremote.models.devices;

public class DigitalDevice extends ArduinoDeviceImpl {
    @SuppressWarnings("unused")
    public DigitalDevice() {

    }

    public DigitalDevice(String name, int pin) {
        super(name, pin);
    }


}

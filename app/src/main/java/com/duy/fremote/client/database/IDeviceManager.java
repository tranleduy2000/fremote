package com.duy.fremote.client.database;

import com.duy.fremote.models.devices.IArduinoDevice;

public interface IDeviceManager {

    void addDevice(IArduinoDevice digitalDevice);

    void removeDevice(IArduinoDevice device);

    void updateDevice(IArduinoDevice device);
}

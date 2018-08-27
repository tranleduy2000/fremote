package com.duy.fremote.server.services;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

interface IBluetoothService {
    void addMessageListener(@NonNull IMessageListener listener);

    void removeMessageListener(@NonNull IMessageListener listener);

    void sendCommand(@NonNull String command);

    void connectBluetoothWith(BluetoothDevice bluetoothDevice);

}

package com.duy.fremote.server.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import com.duy.fremote.models.ResultCallback;

import java.io.IOException;
import java.util.UUID;

public class ConnectBluetoothTask extends AsyncTask<Void, Void, BluetoothSocket> {

    private BluetoothDevice mBluetoothDevice;
    private ResultCallback<BluetoothSocket> mResultCallback;
    private Exception exception;

    ConnectBluetoothTask(BluetoothDevice bluetoothDevice, ResultCallback<BluetoothSocket> resultCallback) {
        this.mBluetoothDevice = bluetoothDevice;
        this.mResultCallback = resultCallback;
    }

    @Override
    protected BluetoothSocket doInBackground(Void... voids) {
        // SPP UUID service
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            BluetoothSocket socket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            socket.connect();
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            this.exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        super.onPostExecute(bluetoothSocket);
        if (isCancelled()) {
            return;
        }
        if (bluetoothSocket != null) {
            mResultCallback.onSuccess(bluetoothSocket);
        } else {
            mResultCallback.onFailure(exception);
        }
    }
}

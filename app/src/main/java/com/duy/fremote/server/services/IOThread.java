package com.duy.fremote.server.services;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;

import com.duy.fremote.server.MessageItem;
import com.duy.fremote.utils.DLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

class IOThread extends Thread {
    private static final String TAG = "ReadThread";
    private BluetoothSocket mSocket;
    private IMessageListener listener;

    private BufferedReader mReader;
    private BufferedWriter mWriter;

    IOThread(@NonNull BluetoothSocket socket, IMessageListener listener) {
        this.mSocket = socket;
        this.listener = listener;

        try {
            mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            mWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            while (!isInterrupted()) {
                String line = mReader.readLine();
                if (listener != null) {
                    listener.onNewMessage(new MessageItem(MessageItem.TYPE_IN, line));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void write(String message) throws IOException {
        if (DLog.DEBUG) DLog.d(TAG, "write() called with: message = [" + message + "]");
        mWriter.write(message);
        mWriter.flush();
    }

    public void disconnect() throws Exception {
        interrupt();
        mWriter.flush();
        mWriter.close();
        mReader.close();
        mSocket.close();
    }

    boolean isConnected() {
        return mSocket.isConnected();
    }
}

package com.duy.fremote.server.services;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.duy.fremote.client.database.DatabaseConstants;
import com.duy.fremote.client.database.SimpleChildEventListener;
import com.duy.fremote.models.ResultCallback;
import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.server.MessageItem;
import com.duy.fremote.server.command.CommandBuilder;
import com.duy.fremote.server.command.CommandConstants;
import com.duy.fremote.utils.DLog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FRemoteService extends Service implements IBluetoothService, IMessageListener {
    private static final String TAG = "FRemoteService";
    private static final int REQUEST_ENV_INTERVAL_TIME = 1000 * 30 /*30s*/;

    private IBinder mBinder;
    @Nullable
    private IConnectListener mConnectListener;
    private ArrayList<IMessageListener> mMessageListeners;
    @Nullable
    private WeakReference<ConnectBluetoothTask> mConnectBluetoothTask;
    @Nullable
    private IOThread mIOThread;

    private DatabaseReference mDatabase;
    private DatabaseReference mDevicesDatabase;
    private ChildEventListener mDevicesStatusListener;

    private Handler mHandler = new Handler();
    private Runnable mRequestEnvironmentInformation = new Runnable() {
        @Override
        public void run() {
            if (isBluetoothConnected()) {
                String cmd = new CommandBuilder().requestHumidity().build();
                sendCommand(cmd);
                cmd = new CommandBuilder().requestTemperature().build();
                sendCommand(cmd);
            }
            mHandler.postDelayed(this, REQUEST_ENV_INTERVAL_TIME);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new FRemoteServiceBinder();
        mMessageListeners = new ArrayList<>();
        mHandler.post(mRequestEnvironmentInformation);
        addDatabaseListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void addDatabaseListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());
        mDevicesDatabase = mDatabase.child(DatabaseConstants.KEY_DEVICES);
        mDevicesStatusListener = new SimpleChildEventListener() {
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (DLog.DEBUG) {
                        DLog.d(TAG, "onChildChanged() called with: dataSnapshot = ["
                                + dataSnapshot + "], s = [" + s + "]");
                    }
                    DigitalDevice value = dataSnapshot.getValue(DigitalDevice.class);
                    if (value == null) {
                        return;
                    }
                    if (value.getValue() != 0) {
                        String cmd = new CommandBuilder().on().pin(value.getPin()).build();
                        sendCommand(cmd);
                    } else {
                        String cmd = new CommandBuilder().off().pin(value.getPin()).build();
                        sendCommand(cmd);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mDevicesDatabase.addChildEventListener(mDevicesStatusListener);
    }

    public boolean isBluetoothConnected() {
        return mIOThread != null && mIOThread.isConnected();
    }

    @Override
    public void addMessageListener(@NonNull IMessageListener listener) {
        mMessageListeners.add(listener);
    }

    @Override
    public void removeMessageListener(@NonNull IMessageListener listener) {
        mMessageListeners.remove(listener);
    }

    @Override
    public void sendCommand(@NonNull String command) {
        try {
            onNewMessage(new MessageItem(MessageItem.TYPE_OUT, command));
            if (mIOThread != null) {
                mIOThread.write(command + "\n");
            } else {
                throw new IOException("Not connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            onNewMessage(new MessageItem(MessageItem.TYPE_ERROR, "Error: " + e.getMessage()));
        }
    }

    @Override
    public void connectBluetoothWith(BluetoothDevice bluetoothDevice) {
        if (DLog.DEBUG)
            DLog.d(TAG, "connectWith() called with: bluetoothDevice = [" + bluetoothDevice + "]");

        if (isBluetoothConnected()) {
            if (DLog.DEBUG) DLog.d(TAG, "connectWith: disconnect");
            disconnect();
        }
        ConnectBluetoothTask connectBluetoothTask = new ConnectBluetoothTask(bluetoothDevice,
                new ResultCallback<BluetoothSocket>() {
                    @Override
                    public void onSuccess(BluetoothSocket result) {
                        onBluetoothConnected(result);
                    }

                    @Override
                    public void onFailure(@Nullable Exception e) {
                        if (e == null) {
                            return;
                        }
                        onNewMessage(new MessageItem(MessageItem.TYPE_ERROR, "Error: " + e.getMessage()));
                    }
                });
        connectBluetoothTask.execute();
        mConnectBluetoothTask = new WeakReference<>(connectBluetoothTask);
    }

    private void onBluetoothConnected(@NonNull BluetoothSocket socket) {
        if (mConnectListener != null) {
            mConnectListener.onConnectStatusChange(socket.isConnected());
        }
        mIOThread = new IOThread(socket, this);
        mIOThread.start();
    }

    public void disconnect() {
        if (mIOThread != null) {
            try {
                mIOThread.disconnect();
                mIOThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mConnectListener != null) {
            mConnectListener.onConnectStatusChange(false);
        }
    }

    public void setConnectListener(@Nullable IConnectListener connectListener) {
        this.mConnectListener = connectListener;
        if (isBluetoothConnected()) {
            if (mConnectListener != null) {
                mConnectListener.onConnectStatusChange(true);
            }
        }
    }

    @WorkerThread
    @Override
    public void onNewMessage(MessageItem message) {
        processCommandFromArduino(message.getContent());
        for (IMessageListener messageListener : mMessageListeners) {
            messageListener.onNewMessage(message);
        }
    }

    private void processCommandFromArduino(String command) {
        if (command == null || command.isEmpty()) {
            return;
        }
        try {
            command = command.replace("  ", " ");
            String[] data = command.split(" ");
            if (data.length == 2) {
                switch (data[0]) {
                    case CommandConstants.GET_HUMIDITY:
                        String humidityValue = data[1];
                        mDatabase.child(DatabaseConstants.KEY_HUMIDITY).setValue(Float.parseFloat(humidityValue));
                        break;
                    case CommandConstants.GET_TEMPERATURE:
                        String tempValue = data[1];
                        mDatabase.child(DatabaseConstants.KEY_TEMPERATURE).setValue(Float.parseFloat(tempValue));
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (DLog.DEBUG) DLog.d(TAG, "onDestroy() called");

        mHandler.removeCallbacks(mRequestEnvironmentInformation);
        if (mDevicesDatabase != null) {
            mDevicesDatabase.removeEventListener(mDevicesStatusListener);
        }
        if (mConnectBluetoothTask != null) {
            ConnectBluetoothTask task = mConnectBluetoothTask.get();
            if (task != null) {
                task.cancel(true);
            }
        }
        disconnect();
        super.onDestroy();
    }

    public class FRemoteServiceBinder extends Binder {
        public FRemoteService getService() {
            return FRemoteService.this;
        }
    }

}

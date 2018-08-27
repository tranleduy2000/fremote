package com.duy.fremote.client.database;

import android.support.annotation.NonNull;

import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.models.devices.IArduinoDevice;
import com.duy.fremote.models.scenes.IScene;
import com.duy.fremote.utils.DLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DatabaseManager implements IDatabaseManager, OnFailureListener {
    private static final String TAG = "DeviceManager";
    public static DatabaseManager instance;
    private DatabaseReference mDevicesDatabase;
    private DatabaseReference mScenesDatabase;

    private DatabaseManager(@NonNull FirebaseUser firebaseUser) {
        DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference()
                .child(firebaseUser.getUid());
        mDevicesDatabase = database.child(DatabaseConstants.KEY_DEVICES);
        mScenesDatabase = database.child(DatabaseConstants.KEY_SCENES);
    }

    public static DatabaseManager getInstance(FirebaseUser firebaseUser) {
        if (instance == null) {
            instance = new DatabaseManager(firebaseUser);
        }
        return instance;
    }

    @Override
    public void addDevice(IArduinoDevice digitalDevice) {
        if (DLog.DEBUG)
            DLog.d(TAG, "addDevice() called with: digitalDevice = [" + digitalDevice + "]");
        mDevicesDatabase.child(String.valueOf(digitalDevice.getPin()))
                .setValue(digitalDevice)
                .addOnFailureListener(this);
    }

    @Override
    public void removeDevice(IArduinoDevice device) {
        if (DLog.DEBUG) DLog.d(TAG, "removeDevice() called with: device = [" + device + "]");
        mDevicesDatabase.child(String.valueOf(device.getPin()))
                .removeValue()
                .addOnFailureListener(this);
    }

    @Override
    public void updateDevice(IArduinoDevice device) {
        if (DLog.DEBUG) DLog.d(TAG, "updateDevice() called with: device = [" + device + "]");
        mDevicesDatabase.child(String.valueOf(device.getPin()))
                .setValue(device)
                .addOnFailureListener(this);
    }

    @Override
    public void applyScene(IScene scene) {
        if (DLog.DEBUG) DLog.d(TAG, "applyScene() called with: scene = [" + scene + "]");
        List<DigitalDevice> devicesStatus = scene.getDevicesStatus();
        for (DigitalDevice device : devicesStatus) {
            updateDevice(device);
        }
    }

    @Override
    public void addScene(IScene scene, OnCompleteListener<Void> onCompleteListener) {
        if (DLog.DEBUG)
            DLog.d(TAG, "addScene() called with: scene = [" + scene + "], onCompleteListener = [" + onCompleteListener + "]");
        mScenesDatabase.child(scene.getName()).setValue(scene)
                .addOnFailureListener(this)
                .addOnCompleteListener(onCompleteListener);
    }

    @Override
    public void updateScene(IScene scene) {
        if (DLog.DEBUG) DLog.d(TAG, "updateScene() called with: scene = [" + scene + "]");
        mScenesDatabase.child(scene.getName())
                .setValue(scene)
                .addOnFailureListener(this);
    }

    @Override
    public void removeScene(IScene scene) {
        mScenesDatabase.child(scene.getName())
                .removeValue()
                .addOnFailureListener(this);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (DLog.DEBUG) DLog.e(TAG, "onFailure: ", e);
    }
}

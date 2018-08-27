package com.duy.fremote.client.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.models.devices.IArduinoDevice;
import com.duy.fremote.models.scenes.IScene;
import com.duy.fremote.models.scenes.Scene;
import com.duy.fremote.utils.DLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements IDatabaseManager, OnFailureListener {
    private static final String TAG = "DeviceManager";
    public static DatabaseManager instance;

    private DatabaseReference mDevicesDatabase;
    private DatabaseReference mScenesDatabase;

    private ArrayList<IArduinoDevice> mDevices = new ArrayList<>();
    private ArrayList<Scene> mScenes = new ArrayList<>();

    private DatabaseManager(@NonNull FirebaseUser firebaseUser) {
        final DatabaseReference database = FirebaseDatabase.getInstance()
                .getReference()
                .child(firebaseUser.getUid());
        mDevicesDatabase = database.child(DatabaseConstants.KEY_DEVICES);
        mScenesDatabase = database.child(DatabaseConstants.KEY_SCENES);
        addWatcher();
    }

    public static DatabaseManager getInstance(FirebaseUser firebaseUser) {
        if (instance == null) {
            instance = new DatabaseManager(firebaseUser);
        }
        return instance;
    }

    private void addWatcher() {
        mDevicesDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    DigitalDevice value = dataSnapshot.getValue(DigitalDevice.class);
                    mDevices.add(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    DigitalDevice newValue = dataSnapshot.getValue(DigitalDevice.class);
                    for (IArduinoDevice device : mDevices) {
                        if (device.getPin() == newValue.getPin()) {
                            device.setValue(newValue.getValue());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                try {
                    DigitalDevice value = dataSnapshot.getValue(DigitalDevice.class);
                    mDevices.remove(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mScenesDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    Scene value = dataSnapshot.getValue(Scene.class);
                    mScenes.add(value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addDevice(IArduinoDevice digitalDevice) {
        if (DLog.DEBUG)
            DLog.d(TAG, "addDevice() called with: digitalDevice = [" + digitalDevice + "]");
        mDevicesDatabase.child(String.valueOf(digitalDevice.getPin()))
                .setValue(digitalDevice)
                .addOnFailureListener(this);
    }

    public ArrayList<IArduinoDevice> getAllDevices() {
        return new ArrayList<>(mDevices);
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

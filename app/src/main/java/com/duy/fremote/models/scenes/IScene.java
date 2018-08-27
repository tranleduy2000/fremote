package com.duy.fremote.models.scenes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.duy.fremote.models.devices.DigitalDevice;

import java.util.List;

public interface IScene {
    List<DigitalDevice> getDevicesStatus();

    void setDevicesStatus(List<DigitalDevice> devicesStatus);

    @NonNull
    String getName();

    @Nullable
    String getDescription();

    long getTimeStart();

    long getTimeEnd();
}

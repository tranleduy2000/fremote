package com.duy.fremote.models.scenes;

import android.support.annotation.Nullable;

import com.duy.fremote.models.devices.DigitalDevice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Scene implements Serializable, IScene {
    private List<DigitalDevice> devicesStatus;
    private String name = "";
    private String description;
    private long timeStart, timeEnd;
    private int iconIndex;

    public Scene() {
        devicesStatus = new ArrayList<>();
    }

    @Override
    public List<DigitalDevice> getDevicesStatus() {
        return devicesStatus;
    }

    @Override
    public void setDevicesStatus(List<DigitalDevice> devicesStatus) {
        this.devicesStatus = devicesStatus;
    }

    @Override
    public int getIconIndex() {
        return iconIndex;
    }

    @Override
    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    @Override
    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }
}

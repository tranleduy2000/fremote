package com.duy.fremote.models.devices;

import android.support.annotation.Nullable;

import java.io.Serializable;

public interface IArduinoDevice extends Serializable {
    int getPin();

    @Nullable
    String getName();

    int getValue();

    void setValue(int value);
}

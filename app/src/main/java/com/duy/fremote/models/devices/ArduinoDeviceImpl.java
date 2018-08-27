package com.duy.fremote.models.devices;

import android.support.annotation.Nullable;

public abstract class ArduinoDeviceImpl implements IArduinoDevice {
    protected int pin;
    protected String name;
    protected int value;
    protected int iconIndex;

    public ArduinoDeviceImpl() {

    }

    public ArduinoDeviceImpl(String name, int pin) {
        this.name = name;
        this.pin = pin;
    }

    @Override
    public int getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    @Override
    public int getPin() {
        return pin;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ArduinoDeviceImpl{" +
                "pin=" + pin +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArduinoDeviceImpl)) return false;

        ArduinoDeviceImpl that = (ArduinoDeviceImpl) o;

        if (getPin() != that.getPin()) return false;
        if (getValue() != that.getValue()) return false;
        if (getIconIndex() != that.getIconIndex()) return false;
        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        int result = getPin();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + getValue();
        result = 31 * result + getIconIndex();
        return result;
    }
}

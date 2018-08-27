package com.duy.fremote.server.command;

import com.duy.fremote.client.database.DatabaseManager;
import com.duy.fremote.models.devices.IArduinoDevice;

import java.util.List;
import java.util.regex.Pattern;

public class ViCommandProcessor implements ICommandProcessor {

    private DatabaseManager mDatabaseManager;

    public ViCommandProcessor(DatabaseManager databaseManager) {
        this.mDatabaseManager = databaseManager;
    }

    @Override
    public boolean process(String command, List<IArduinoDevice> devices) {
        for (IArduinoDevice device : devices) {
            String name = device.getName();
            if (name != null && !name.trim().isEmpty()) {
                //turn on device
                Pattern pattern = Pattern.compile("(.*?)(bật|mở)(.*?)" + makeNamePattern(name),
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                if (pattern.matcher(command).find()) {
                    device.setValue(1);
                    mDatabaseManager.updateDevice(device);
                    return true;
                }

                //turn off device
                pattern = Pattern.compile("(.*?)(tắt|đóng)(.*?)" + makeNamePattern(name) + "( )?(.*?)",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                if (pattern.matcher(command).find()) {
                    device.setValue(0);
                    mDatabaseManager.updateDevice(device);
                    return true;
                }
            }

            //turn on
            Pattern pattern = Pattern.compile("(.*?)(bật|mở)(.*?) (" + device.getPin() + ")$",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            if (pattern.matcher(command).find()) {
                device.setValue(1);
                mDatabaseManager.updateDevice(device);
                return true;
            }

            //turn off
            pattern = Pattern.compile("(.*?)(tắt|đóng)(.*?) (" + device.getPin() + ")$",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            if (pattern.matcher(command).find()) {
                device.setValue(0);
                mDatabaseManager.updateDevice(device);
                return true;
            }
        }
        return false;
    }

    private String makeNamePattern(String name) {
        name = name.replace("  ", " ");
        String[] words = name.split(" ");
        StringBuilder pattern = new StringBuilder();
        for (String word : words) {
            pattern.append("(").append(word).append(")(.*?)");
        }
        return pattern.toString();
    }
}

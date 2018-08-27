package com.duy.fremote.server.command;

import com.duy.fremote.models.devices.IArduinoDevice;
import com.duy.fremote.models.scenes.IScene;

import java.util.List;

interface ICommandProcessor {
    boolean process(String command, List<IArduinoDevice> devices);
}

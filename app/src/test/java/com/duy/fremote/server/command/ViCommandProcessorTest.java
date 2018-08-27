package com.duy.fremote.server.command;

import com.duy.fremote.models.devices.DigitalDevice;
import com.duy.fremote.models.devices.IArduinoDevice;
import com.duy.fremote.models.scenes.IScene;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class ViCommandProcessorTest extends TestCase {

    public void testOn() {
        ViCommandProcessor processor = new ViCommandProcessor(mDatabaseManager);
        List<IArduinoDevice> deviceList = getDeviceList();
        List<IScene> sceneList = getSceneList();

        assertEquals(processor.process("Bật đèn số 2", deviceList, sceneList), "O 2");
        assertEquals(processor.process("Bật đèn số 3", deviceList, sceneList), "O 3");
        assertEquals(processor.process("Bật đèn số 5", deviceList, sceneList), "O 5");
        assertEquals(processor.process("Ê Bật đèn số 5", deviceList, sceneList), "O 5");
        assertEquals(processor.process("Giúp tao bật đèn phòng khách", deviceList, sceneList), "O 2");
        assertEquals(processor.process("Giúp tao bật đèn trong phòng khách", deviceList, sceneList), "O 2");
    }

    private List<IScene> getSceneList() {
        return new ArrayList<>();
    }

    private List<IArduinoDevice> getDeviceList() {
        ArrayList<IArduinoDevice> devices = new ArrayList<>();
        devices.add(new DigitalDevice("Đèn phòng khách", 2));
        devices.add(new DigitalDevice("Đèn số 3", 3));
        devices.add(new DigitalDevice("Quạt trước nhà", 4));
        devices.add(new DigitalDevice("Đèn số 5", 5));
        return devices;
    }
}
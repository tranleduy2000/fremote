package com.duy.fremote.client.database;

import com.duy.fremote.R;

public class DatabaseConstants {
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_TEMPERATURE = "temperature";

    public static final String KEY_DEVICES = "devices";
    public static final String KEY_SCENES = "scenes";

    public static final Integer[] DEVICE_ICON_IDS = new Integer[]{
            R.drawable.ic_device_fan_128,
            R.drawable.ic_device_light_128,
    };

    public static final Integer[] SCENE_ICON_IDS = new Integer[]{
            R.drawable.ic_scene_home_128,
            R.drawable.ic_scene_get_up_128,
            R.drawable.ic_scene_living_room_128,
            R.drawable.ic_scene_work_128,
            R.drawable.ic_scene_go_to_bed_128,
    };
}

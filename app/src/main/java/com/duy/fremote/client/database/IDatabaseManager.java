package com.duy.fremote.client.database;

import com.duy.fremote.models.scenes.IScene;

public interface IDatabaseManager extends IDeviceManager, ISceneManager {




    void applyScene(IScene scene);
}

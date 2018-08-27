package com.duy.fremote.client.database;

import com.duy.fremote.models.scenes.IScene;
import com.google.android.gms.tasks.OnCompleteListener;

public interface ISceneManager {
    void addScene(IScene scene, OnCompleteListener<Void> onCompleteListener);

    void removeScene(IScene scene);

    void updateScene(IScene scene);
}

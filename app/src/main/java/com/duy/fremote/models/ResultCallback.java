package com.duy.fremote.models;

import android.support.annotation.Nullable;

public interface ResultCallback<T> {
    void onSuccess(T result);

    void onFailure(@Nullable Exception e);
}

package com.duy.fremote.server.services;

import android.support.annotation.WorkerThread;

import com.duy.fremote.server.MessageItem;


public interface IMessageListener {
    @WorkerThread
    void onNewMessage(MessageItem message);
}

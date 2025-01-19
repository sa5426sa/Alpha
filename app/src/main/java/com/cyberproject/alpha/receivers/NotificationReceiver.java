package com.cyberproject.alpha.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.cyberproject.alpha.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        String text = intent.getStringExtra("message");
        NotificationHelper.showNotification(context, text);
    }
}
package com.example.emotionaldiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class DiaryReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        "AppSettings",
                        Context.MODE_PRIVATE
                );

        boolean notificationEnabled =
                preferences.getBoolean(
                        "notification",
                        true
                );

        if (!notificationEnabled) {
            return;
        }

        NotificationHelper.createNotificationChannel(context);

        NotificationHelper.showNotification(
                context,
                "Emotional Diary",
                "Don't forget to write today's diary."
        );
    }
}
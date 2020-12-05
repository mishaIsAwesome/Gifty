package com.inti.gifty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPreferences.getBoolean("notifications", true);
        if (!enabled)
            return;

        String eventName = intent.getStringExtra("eventName");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "remindEvent")
                .setSmallIcon(R.drawable.ic_gift)
                .setContentTitle("Upcoming Event")
                .setContentText(eventName + " is coming up soon!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }
}

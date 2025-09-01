package com.example.immunobubby;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "FarmaciAlarmReceiver";
    private static final String CHANNEL_ID = "medicine_channel";
    private static final int NOTIFICATION_ID_BASE = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name");
        int requestCode = intent.getIntExtra("requestCode", 0);

        Log.d(TAG, "Alarm ricevuto! name=" + name + ", requestCode=" + requestCode);

        createNotificationChannel(context);

        // Intent per aprire l'app
        Intent openAppIntent = new Intent(context, FarmaciActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.pill_24px)
                .setContentTitle("Promemoria farmaco")
                .setContentText(name != null ? name : "È ora di prendere il farmaco")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID_BASE + requestCode, builder.build());
            Log.d(TAG, "Notifica mostrata: requestCode=" + requestCode);
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Promemoria farmaci",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canale per notifiche promemoria farmaci");
            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "NotificationChannel creato: " + CHANNEL_ID);
            }
        }
    }
}

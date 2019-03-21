package com.example.ipscan.lib.applied;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.ipscan.MainActivity;
import com.example.ipscan.R;
import com.example.ipscan.lib.Const;

public class ServiceUtils {
  public static Notification createNotification(Context ctx, int titleRes, int descrRes) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = ctx.getString(R.string.channel_name);
      String description = ctx.getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(Const.CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }

    Intent notificationIntent = new Intent(ctx.getApplicationContext(), MainActivity.class);
    notificationIntent.setAction(Intent.ACTION_MAIN);  // A string containing the action name
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent contentPendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);

    return new NotificationCompat.Builder(ctx, Const.CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(ctx.getResources().getString(titleRes))
      .setContentText(ctx.getResources().getString(descrRes))
      .setContentIntent(contentPendingIntent)
      .setOngoing(true)
      .build();
  }
}

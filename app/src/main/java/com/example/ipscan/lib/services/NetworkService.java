package com.example.ipscan.lib.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ipscan.MainActivity;
import com.example.ipscan.R;
import com.example.ipscan.lib.Const;


public class NetworkService extends Service {
  static final int NOTIFICATION_ID = 543;
  public static boolean isServiceRunning = false;

  private AlarmManager alarmMgr;
  private PendingIntent alarmIntent;
  ConnectivityManager connectivityManager;
  ActivityManager activityManager;

  @Override
  public void onCreate() {
    super.onCreate();
    goToForegroundMode();
    alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(NetworkService.this, NetworkService.class);
    intent.setAction(Const.ACTION_SET_NETWORK_SERVICE_ALARM);
    alarmIntent = PendingIntent.getService(this, 0, intent, 0);

    alarmMgr.setInexactRepeating(
      AlarmManager.RTC
      , System.currentTimeMillis()
      , 60 * 1000, alarmIntent);
    Log.d(Const.LOG_TAG, "Alarm was set");

    connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

    collectDeviceInfo();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(Const.LOG_TAG, "NetworkService onStartCommand");
    if (intent != null) {
      if (intent.getAction().equals(Const.ACTION_START_NETWORK_SERVICE)) {
        goToForegroundMode();
      } else {
        if (intent.getAction().equals(Const.ACTION_SET_NETWORK_SERVICE_ALARM)) {
          handleAlarm();
        } else {
          stopService();
        }
      }
    } else {
      stopService();
    }

    return START_STICKY;
  }

  // In case the service is deleted or crashes some how
  @Override
  public void onDestroy() {
    isServiceRunning = false;
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    // Used only in case of bound services.
    return null;
  }


  void goToForegroundMode() {
    if (isServiceRunning) {
      return;
    }
    isServiceRunning = true;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(Const.CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }

    Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
    notificationIntent.setAction(Intent.ACTION_MAIN);  // A string containing the action name
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

//    Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
    Notification notification = new NotificationCompat.Builder(this)
      .setContentTitle(getResources().getString(R.string.app_name))
      .setTicker(getResources().getString(R.string.app_name))
      .setContentText(getResources().getString(R.string.add_scan_job))
      .setSmallIcon(R.mipmap.ic_launcher)
//      .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
      .setContentIntent(contentPendingIntent)
      .setOngoing(true)
      .setChannelId(Const.CHANNEL_ID)
      .build();

    //TODO check it
    // NO_CLEAR makes the notification stay when the user performs a "delete all" command
    notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;

    startForeground(NOTIFICATION_ID, notification);
  }

  void stopService() {
    stopForeground(true);
    stopSelf();
    isServiceRunning = false;
  }

  private void handleAlarm() {
    Log.d(Const.LOG_TAG, "handleAlarm called!!!");
  }

  private void collectDeviceInfo() {
    //Anything lower than 4 cpu's  would be consider a slower processing device.
    int cpuCount = Runtime.getRuntime().availableProcessors();
    Log.d(Const.LOG_TAG, "cpuCount: " + cpuCount);
    //this generally checks and return true for devices with lower than 1GB memory
    boolean isLowRamdevice = activityManager.isLowRamDevice();
    Log.d(Const.LOG_TAG, "isLowRamDevice: " + isLowRamdevice);

    String deviceFullName = Build.MANUFACTURER
      + " " + Build.MODEL + " " + Build.VERSION.RELEASE
      + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    Log.d(Const.LOG_TAG, "deviceFullName: " + deviceFullName);
  }

  private String getNetworkType() {
    return connectivityManager.getActiveNetworkInfo().getTypeName();
  }
}
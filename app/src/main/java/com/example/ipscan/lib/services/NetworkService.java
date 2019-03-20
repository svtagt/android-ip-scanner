package com.example.ipscan.lib.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ipscan.MainActivity;
import com.example.ipscan.R;
import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.api.FetchDataListener;
import com.example.ipscan.lib.applied.DeviceInfo;
import com.example.ipscan.lib.requests.MainRequests;

import org.json.JSONException;
import org.json.JSONObject;


public class NetworkService extends Service {
  static final int NOTIFICATION_ID = 543;
  public static boolean isServiceRunning = false;

  private AlarmManager alarmMgr;
  private PendingIntent alarmIntent;
  private DeviceInfo deviceInfo;

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
    deviceInfo = new DeviceInfo(getApplicationContext());
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
    new MainRequests().getTask(deviceInfo, new FetchDataListener() {
      @Override
      public void onFetchSuccess(int status, JSONObject res) throws JSONException {
        Log.d(Const.LOG_TAG, "SUCCESS! status: " + status + " RES: " + res.toString());
        startScanService(res.getString("taskId"), res.getString("cmd"));
//        startScanService(res.getString("taskId"), "-h 62.109.9.97-62.109.9.98 -p 1-1024");
      }

      @Override
      public void onFetchFailed(int status, JSONObject res) {
        Log.d(Const.LOG_TAG, "FAILED! status: " + status + " RES: " + res.toString());
      }

      @Override
      public <T extends Throwable> void onFetchError(T err) {
        Log.e(Const.LOG_TAG, "onFetchError! " + err.toString());
      }
    });
  }


  private void startScanService(String taskId, String cmd) {
    Intent intent = new Intent(this, ScanService.class);
    intent.putExtra(Const.EXTRA_TASK_ID, taskId);
    intent.putExtra(Const.EXTRA_SCAN_PARAMS, cmd);
    startService(intent);
  }
}
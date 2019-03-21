package com.example.ipscan.lib.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.ipscan.R;
import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.api.FetchDataListener;
import com.example.ipscan.lib.applied.DeviceInfo;
import com.example.ipscan.lib.applied.ServiceUtils;
import com.example.ipscan.lib.requests.MainRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class GetScanJobService extends Service {
  public static boolean isServiceRunning = false;

  private AlarmManager alarmMgr;
  private PendingIntent alarmIntent;
  private DeviceInfo deviceInfo;

  @Override
  public void onCreate() {
    super.onCreate();

    alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(GetScanJobService.this, GetScanJobService.class);
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
    Log.d(Const.LOG_TAG, "GetScanJobService onStartCommand");
    if (intent != null) {
      if (Objects.equals(intent.getAction(), Const.ACTION_START_NETWORK_SERVICE)) {
        goToForegroundMode();
      } else {
        if (Objects.equals(intent.getAction(), Const.ACTION_SET_NETWORK_SERVICE_ALARM)) {
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

  @Override
  public void onDestroy() {
    isServiceRunning = false;
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  void goToForegroundMode() {
    if (isServiceRunning) {
      return;
    }
    isServiceRunning = true;
    startForeground(Const.GET_SCAN_JOB_SERVICE_NOTIFICATION_ID, ServiceUtils.createNotification(
      this, R.string.get_scan_job_service_title, R.string.get_scan_job_service_descr));
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
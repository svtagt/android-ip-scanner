package com.example.ipscan.lib.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.ipscan.R;
import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.IPScan;
import com.example.ipscan.lib.api.FetchDataListener;
import com.example.ipscan.lib.api.Http;
import com.example.ipscan.lib.applied.DeviceInfo;
import com.example.ipscan.lib.applied.ServiceUtils;
import com.example.ipscan.lib.requests.MainRequests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;


public class NetworkingService extends Service {
  public static boolean isServiceRunning = false;

  private DeviceInfo deviceInfo;
  private BroadcastReceiver broadcastReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
    deviceInfo = new DeviceInfo(getApplicationContext());
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(Const.LOG_TAG, "NetworkingService onStartCommand");
    if (intent != null) {
      if (Objects.equals(intent.getAction(), Const.ACTION_HANDLE_ALARM)) {
        handleAlarm();
        goToForegroundMode();
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
    Log.d(Const.LOG_TAG, "NetworkingService handleAlarm called.");
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
    if (broadcastReceiver != null) {
      unregisterReceiver(broadcastReceiver);
      broadcastReceiver = null;
    }

    broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        File reportFile = (File) Objects.requireNonNull(intent.getExtras()).get(Const.EXTRA_REPORT_FILE);
        Http.postFile("/result", reportFile, new FetchDataListener() {
          @Override
          public void onFetchSuccess(int status, JSONObject res) {
            Log.d(Const.LOG_TAG, "NetworkingService SUCCESS! status: " + status + " RES: " + res.toString());
          }

          @Override
          public void onFetchFailed(int status, JSONObject res) {
            Log.d(Const.LOG_TAG, "NetworkingService FAILED! status: " + status + " RES: " + res.toString());
          }

          @Override
          public <T extends Throwable> void onFetchError(T err) {
            Log.e(Const.LOG_TAG, "NetworkingService onFetchError! " + err.toString());
          }
        });
      }
    };

    IPScan ipScan = new IPScan(this);
    ipScan.scan(cmd, taskId, broadcastReceiver);
  }
}
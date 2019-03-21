package com.example.ipscan.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.example.ipscan.lib.services.NetworkingService;
import com.example.ipscan.lib.services.ScanService;

public class IPScan {

  private Context ctx;
  private AlarmManager alarmMgr;
  private PendingIntent alarmIntent;

  public IPScan(Context ctx) {
    this.ctx = ctx;
    this.alarmMgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
  }

  public void scan(String paramsStr, String taskId, BroadcastReceiver broadcastReceiver) {
    IntentFilter intentFilter = new IntentFilter(Const.BROADCAST_ACTION);
    ctx.registerReceiver(broadcastReceiver, intentFilter);

    Intent intent = new Intent(ctx, ScanService.class);
    intent.putExtra(Const.EXTRA_SCAN_PARAMS, paramsStr);
    intent.putExtra(Const.EXTRA_TASK_ID, taskId);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      ctx.startForegroundService(intent);
    } else {
      ctx.startService(intent);
    }
  }

  public void scan(String paramsStr, BroadcastReceiver broadcastReceiver) {
    this.scan(paramsStr, null, broadcastReceiver);
  }

  public void scan(String paramsStr, String taskId) {
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        //TODO need to set real path to reports folder
        Log.d(Const.LOG_TAG, "Scanning paramsStr: " + paramsStr + "was finihed. See dir Documents/...");
      }
    };
    this.scan(paramsStr, broadcastReceiver);
  }

  public void startWaitingForTasks(Context ctx, String backendUrl, int requestNewTaskTimeout) {
    if (alarmIntent != null) {
      alarmMgr.cancel(alarmIntent);
    }

    Intent intent = new Intent(ctx, NetworkingService.class);
    intent.setAction(Const.ACTION_HANDLE_ALARM);
    intent.putExtra(Const.EXTRA_BACKEND_URL,backendUrl);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      this.alarmIntent = PendingIntent.getForegroundService(ctx, 0, intent, 0);
    } else {
      this.alarmIntent = PendingIntent.getService(ctx, 0, intent, 0);
    }

    if (requestNewTaskTimeout < 1) {
      requestNewTaskTimeout = 1;
    }

    alarmMgr.setInexactRepeating(
      AlarmManager.RTC
      , System.currentTimeMillis()
      , requestNewTaskTimeout * 60 * 1000, alarmIntent);
    Log.d(Const.LOG_TAG, "Alarm was set");
  }
}

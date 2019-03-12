package com.example.ipscan.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;

import com.example.ipscan.lib.IPAddress;
import com.example.ipscan.lib.port.ScanHostsRunnable;
import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.utils.Const;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanHostsService extends Service {
  ExecutorService es;
  private long startTime;
  private long finishTime;

  private boolean serviceIsBusy;
  
  @Override
  public void onCreate() {
    // The service is being created
    Log.d(Const.LOG_TAG, "ScanHostsService onCreate");
    es = Executors.newFixedThreadPool(1);
    serviceIsBusy = false;

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // The service is starting, due to a call to startService()

    if (!serviceIsBusy) {
      serviceIsBusy = true;
      String hostFrom = intent.getStringExtra(Const.EXTRA_HOST_FROM);
      String hostTo = intent.getStringExtra(Const.EXTRA_HOST_TO);
      int portFrom = intent.getIntExtra(Const.EXTRA_PORT_FROM, -1);
      int portTo = intent.getIntExtra(Const.EXTRA_PORT_TO, -1);

      Log.d(Const.LOG_TAG, "hostFrom: " + hostFrom);
      Log.d(Const.LOG_TAG, "hostTo: " + hostTo);
      Log.d(Const.LOG_TAG, "portFrom: " + portFrom);
      Log.d(Const.LOG_TAG, "portTo: " + portTo);

      if (hostFrom != null && hostTo != null && portFrom >= 0 && portTo > portFrom) {
        startTime = System.nanoTime();
        es.execute(new ScanHostsRunnable(
          new IPAddress(hostFrom), new IPAddress(hostTo), portFrom, portTo, Const.WAN_SOCKET_TIMEOUT,
          new PortScanResult() {
            @Override
            public <T extends Throwable> void processFinish(T err) {
              Log.e(Const.LOG_TAG, "ERROR! : " + err.toString());
            }

            @Override
            public void portWasTimedOut(String host, int portNumber) {
              Log.e(Const.LOG_TAG, "ScanHostsService portWasTimedOut ip: " + host + ", port: " + portNumber);
            }

            @Override
            public void foundClosedPort(String host, int portNumber) {
              Log.e(Const.LOG_TAG, "ScanHostsService foundClosedPort ip: " + host + ", port: " + portNumber);
            }

            @Override
            public void foundOpenPort(String host, SparseArray<String> openPortData) {
              int scannedPort = openPortData.keyAt(0);
              String item = String.valueOf(scannedPort);
              Log.d(Const.LOG_TAG, "OPEN PORT ip: " + host + ", scannedPort:" + scannedPort);
            }

            @Override
            public void processFinish(boolean success) {
              finishTime = System.nanoTime();
              long duration = (finishTime - startTime) / 1000000;
              Log.d(Const.LOG_TAG, "ScanHostsService success:" + success + " finished at: " + TimeUnit.MILLISECONDS.toMinutes(duration) + " min (" + duration + "ms)");
              serviceIsBusy = false;
            }
          }
        ));
      } else {
        //TODO throw error
      }
    } else {
      Log.e(Const.LOG_TAG, "ScanHostsService service is busy!!!!");
    }

    Log.d(Const.LOG_TAG, "ScanHostsService onStartCommand");
    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    // A client is binding to the service with bindService()
    return null;
  }

  @Override
  public void onDestroy() {
    // The service is no longer used and is being destroyed
    Log.d(Const.LOG_TAG, "ScanHostsService onDestroy");
  }

}

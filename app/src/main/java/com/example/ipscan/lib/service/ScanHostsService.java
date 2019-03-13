package com.example.ipscan.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.IPAddress;
import com.example.ipscan.lib.port.ScanHostsRunnable;
import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.utils.FS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanHostsService extends Service {
  ExecutorService es;
  private long startTime;
  private long finishTime;
  private boolean serviceIsBusy;

  IPAddress hostFrom;
  IPAddress hostTo;

  private File fileForResults;
  
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
      //check availability of external storage
      if (FS.isExternalStorageWritable()) {
        Log.d(Const.LOG_TAG, "FS: DIRECTORY_DOCUMENTS: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));

        serviceIsBusy = true;
        String hostFromStr = intent.getStringExtra(Const.EXTRA_HOST_FROM);
        String hostToStr = intent.getStringExtra(Const.EXTRA_HOST_TO);
        int portFrom = intent.getIntExtra(Const.EXTRA_PORT_FROM, -1);
        int portTo = intent.getIntExtra(Const.EXTRA_PORT_TO, -1);

        Log.d(Const.LOG_TAG, "hostFrom: " + hostFromStr);
        Log.d(Const.LOG_TAG, "hostTo: " + hostToStr);
        Log.d(Const.LOG_TAG, "portFrom: " + portFrom);
        Log.d(Const.LOG_TAG, "portTo: " + portTo);

        if (hostFromStr != null && hostToStr != null && portFrom >= 0 && portTo > portFrom) {
          startTime = System.nanoTime();

          hostFrom = new IPAddress(hostFromStr);
          hostTo = new IPAddress(hostToStr);

          es.execute(new ScanHostsRunnable(hostFrom, hostTo, portFrom, portTo, Const.WAN_SOCKET_TIMEOUT,
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
              public void foundOpenPort(String host, int portNumber, String banner) {
                Log.d(Const.LOG_TAG, "ScanHostsService foundOpenPort host: " + host + ", port: " + portNumber + ", banner: " + banner);
              }

              @Override
              public void processFinish(boolean success) {
                finishTime = System.nanoTime();
                long duration = (finishTime - startTime) / 1000000;
                Log.d(Const.LOG_TAG, "ScanHostsService success:" + success + " finished at: " + TimeUnit.MILLISECONDS.toMinutes(duration) + " min (" + duration + "ms)");

                fileForResults = new File(FS.getReportsDir(), FS.generateDocName(hostFromStr, hostToStr, portFrom, portTo));
                try (PrintWriter writer = new PrintWriter(fileForResults)) {
                  StringBuilder sb = new StringBuilder();
                  sb.append("id");
                  sb.append(';');
                  sb.append("Name");
                  sb.append('\n');

                  sb.append("1");
                  sb.append(';');
                  sb.append("Prashant Ghimire");
                  sb.append('\n');

                  writer.write(sb.toString());
                  writer.close();

                  System.out.println("done!");

                } catch (FileNotFoundException e) {
                  System.out.println(e.getMessage());
                }

                serviceIsBusy = false;
              }
            }
          ));
        } else {
          //TODO throw error
        }
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

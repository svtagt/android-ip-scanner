package com.example.ipscan.lib.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.ipscan.MainActivity;
import com.example.ipscan.R;
import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.async.InitScanRunnable;
import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;
import com.example.ipscan.lib.helpers.PortScanReport;
import com.example.ipscan.lib.result.ScanHandler;
import com.example.ipscan.lib.applied.ParamsParser;
import com.example.ipscan.lib.applied.Reports;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanService extends Service {
  ExecutorService es;
  private long startTime;
  private long finishTime;
  private boolean serviceIsBusy;

  private String paramsStr;
  private ArrayList<PortRange> portRangesToScan;
  private ArrayList<Host> hostsToScan;

  ArrayList<String> resultData;
  private File fileForResults;

  long totalItems = 0;
  long currentItem;

  @Override
  public void onCreate() {
    Log.d(Const.LOG_TAG, "ScanService onCreate");
    es = Executors.newFixedThreadPool(1);
    serviceIsBusy = false;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(Const.LOG_TAG, "ScanService onStartCommand");
    if (!serviceIsBusy) {
      //get params string and parse it
      this.paramsStr = intent.getStringExtra(Const.EXTRA_SCAN_PARAMS);
      if (paramsStr != null) {
        this.portRangesToScan = ParamsParser.makePortRangesList(ParamsParser.extractPorts(paramsStr));
        this.hostsToScan = ParamsParser.makeHostsList(ParamsParser.extractHosts(paramsStr));

        //check availability of external storage
        if (Reports.isExternalStorageWritable()) {
          if (hostsToScan.size() > 0 && portRangesToScan.size() > 0) {
            //need show foreground notification
            goToForegroundMode();

            serviceIsBusy = true;
            totalItems = PortScanReport.measure(hostsToScan, portRangesToScan);
            currentItem = 0;
            resultData = new ArrayList<>((int) totalItems);
            startTime = System.nanoTime();
            es.execute(new InitScanRunnable(hostsToScan, portRangesToScan, Const.WAN_SOCKET_TIMEOUT,
              new ScanHandler() {
                @Override
                public <T extends Throwable> void processFinish(T err) {
                  Log.e(Const.LOG_TAG, "ERROR! : " + err.toString());
                }

                @Override
                public void portWasTimedOut(Host host, int portNumber) {
                  currentItem++;
                  resultData.add(PortScanReport.add(host, portNumber, PortScanReport.portIsTimedOut, null));
                }

                @Override
                public void foundClosedPort(Host host, int portNumber) {
                  currentItem++;
                  resultData.add(PortScanReport.add(host, portNumber, PortScanReport.portIsClosed, null));
                }

                @Override
                public void foundOpenPort(Host host, int portNumber, String banner) {
                  currentItem++;
                  Log.d(Const.LOG_TAG,
                    "REPORT (" + currentItem + "/" + totalItems + ") Open - host: " + host
                      + ", port: " + portNumber + ", banner: " + banner);
                  resultData.add(PortScanReport.add(host, portNumber, PortScanReport.portIsOpen, banner));
                }

                @Override
                public void processItem() {
//                  Log.d(Const.LOG_TAG, "Processed: " + currentItem + "/" + totalItems);
                }

                @Override
                public void processFinish(boolean success) {
                  finishTime = System.nanoTime();
                  long duration = (finishTime - startTime) / 1000000;
                  Log.d(Const.LOG_TAG, "REPORT success:" + success + " finished at: " + TimeUnit.MILLISECONDS.toMinutes(duration) + " min (" + duration + "ms)");

                  fileForResults = new File(Reports.getReportsDir(), Reports.generateDocName(paramsStr));
                  PortScanReport.write(resultData, fileForResults);

                  serviceIsBusy = false;
                  stopForeground(true);
                  stopSelf(startId);
                }
              }
            ));
          } else {
            //TODO throw error
          }
        } else {
          //TODO throw error
        }
      }

    } else {
      Log.e(Const.LOG_TAG, "ScanService service is busy!!!!");
    }

    return START_STICKY;
  }

  private void goToForegroundMode() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
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

    Intent resultIntent = new Intent(this, MainActivity.class);
    PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
      resultIntent, 0);


    Notification notification =
      new NotificationCompat.Builder(this)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("My notification")
        .setContentText("Hello World!")
        .setContentIntent(resultPendingIntent)
        .setOngoing(true)
        .setChannelId(Const.CHANNEL_ID)
        .build();

    startForeground(Const.ONGOING_NOTIFICATION_ID, notification);
  }

  @Override
  public IBinder onBind(Intent intent) {
    // A client is binding to the service with bindService()
    return null;
  }

  @Override
  public void onDestroy() {
    stopForeground(true);
    stopSelf();
    serviceIsBusy = false;
    Log.d(Const.LOG_TAG, "ScanService onDestroy");
  }
}

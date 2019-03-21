package com.example.ipscan.lib.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.ipscan.R;
import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.applied.ParamsParser;
import com.example.ipscan.lib.applied.Reports;
import com.example.ipscan.lib.applied.ServiceUtils;
import com.example.ipscan.lib.async.InitScanRunnable;
import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;
import com.example.ipscan.lib.helpers.PortScanReport;
import com.example.ipscan.lib.result.ScanHandler;

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
  private String taskId;

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

        this.taskId = intent.getStringExtra(Const.EXTRA_TASK_ID) != null
          ? intent.getStringExtra(Const.EXTRA_TASK_ID)
          : paramsStr;

        Log.d(Const.LOG_TAG, "taskId: " + taskId);
        Log.d(Const.LOG_TAG, "paramsStr: " + paramsStr);

        if (Reports.isExternalStorageWritable()) {
          if (hostsToScan.size() > 0 && portRangesToScan.size() > 0) {
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
                  Log.d(Const.LOG_TAG, "REPORT success:" + success
                    + " finished at: " + TimeUnit.MILLISECONDS.toMinutes(duration)
                    + " min (" + duration + "ms)");

                  fileForResults = new File(Reports.getReportsDir(), Reports.setReportName(taskId));
                  PortScanReport.write(resultData, fileForResults);

                  Intent finishIntent = new Intent(Const.BROADCAST_ACTION);
                  finishIntent.putExtra(Const.EXTRA_REPORT_FILE, fileForResults);
                  sendBroadcast(finishIntent);
                  finishWork();
                }
              }
            ));
          } else {
            Log.e(Const.LOG_TAG, "There are no data to scan!");
          }
        } else {
          String e = "External Storage not writable!";
          Log.e(Const.LOG_TAG, e);
          throw new Error(e);
        }
      }

    } else {
      Log.e(Const.LOG_TAG, "ScanService service is busy!");
    }

    return START_STICKY;
  }

  private void goToForegroundMode() {
    startForeground(Const.SCAN_SERVICE_NOTIFICATION_ID, ServiceUtils.createNotification(
      this, R.string.scan_service_title, R.string.scan_service_descr));
  }

  private void finishWork() {
    Log.d(Const.LOG_TAG, "ScanService finishWork");
    serviceIsBusy = false;
    stopForeground(true);
    stopSelf();
  }

  @Override
  public IBinder onBind(Intent intent) {
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

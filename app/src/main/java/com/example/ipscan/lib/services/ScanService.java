package com.example.ipscan.lib.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.async.ScanRunnable;
import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;
import com.example.ipscan.lib.helpers.PortScanReport;
import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.utils.ParamsParser;
import com.example.ipscan.lib.utils.Reports;

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
    // The service is being created
    Log.d(Const.LOG_TAG, "ScanService onCreate");
    es = Executors.newFixedThreadPool(1);
    serviceIsBusy = false;

  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // The service is starting, due to a call to startService()

    if (!serviceIsBusy) {
      //get params string and parse it
      this.paramsStr = intent.getStringExtra(Const.EXTRA_SCAN_PARAMS);
      if (paramsStr != null) {
        //parse params
        this.portRangesToScan = ParamsParser.makePortRangesList(ParamsParser.extractPorts(paramsStr));
        this.hostsToScan = ParamsParser.makeHostsList(ParamsParser.extractHosts(paramsStr));
        //check availability of external storage
        if (Reports.isExternalStorageWritable()) {
          if (hostsToScan.size() > 0 && portRangesToScan.size() > 0) {
            serviceIsBusy = true;
            startTime = System.nanoTime();
//            resultData = new ArrayList<>();
            totalItems = PortScanReport.measure(hostsToScan, portRangesToScan);
            resultData = new ArrayList<>((int) totalItems);

            es.execute(new ScanRunnable(hostsToScan, portRangesToScan, Const.WAN_SOCKET_TIMEOUT,
              new PortScanResult() {
                @Override
                public <T extends Throwable> void processFinish(T err) {
                  Log.e(Const.LOG_TAG, "ERROR! : " + err.toString());
                }

                @Override
                public void portWasTimedOut(String host, int portNumber) {
                  currentItem++;
                Log.d(Const.LOG_TAG, "REPORT (" + currentItem + "/" + totalItems + ") TimedOut - host: " + host + ", port: " + portNumber);
                  resultData.add(PortScanReport.add(new Host(host), portNumber, PortScanReport.portIsTimedOut, null));
                }

                @Override
                public void foundClosedPort(String host, int portNumber) {
                  currentItem++;
                Log.d(Const.LOG_TAG, "REPORT (" + currentItem + "/" + totalItems + ") Closed - host: " + host + ", port: " + portNumber);
                  resultData.add(PortScanReport.add(new Host(host), portNumber, PortScanReport.portIsClosed, null));
                }

                @Override
                public void foundOpenPort(String host, int portNumber, String banner) {
                  currentItem++;
                Log.d(Const.LOG_TAG, "REPORT (" + currentItem + "/" + totalItems + ") Open - host: " + host + ", port: " + portNumber + ", banner: " + banner);
                  resultData.add(PortScanReport.add(new Host(host), portNumber, PortScanReport.portIsOpen, banner));
                }

                @Override
                public void processItem() {
                  Log.d(Const.LOG_TAG, "Processed: " + currentItem + "/" + totalItems);
                }

                @Override
                public void processFinish(boolean success) {
                  finishTime = System.nanoTime();
                  long duration = (finishTime - startTime) / 1000000;
                  Log.d(Const.LOG_TAG, "REPORT success:" + success + " finished at: " + TimeUnit.MILLISECONDS.toMinutes(duration) + " min (" + duration + "ms)");

                  fileForResults = new File(Reports.getReportsDir(), Reports.generateDocName(paramsStr));
                  PortScanReport.write(resultData, fileForResults);

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
      }

    } else {
      Log.e(Const.LOG_TAG, "ScanService service is busy!!!!");
    }

    Log.d(Const.LOG_TAG, "ScanService onStartCommand");
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
    Log.d(Const.LOG_TAG, "ScanService onDestroy");
  }

}

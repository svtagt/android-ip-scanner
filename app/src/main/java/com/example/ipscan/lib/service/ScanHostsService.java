package com.example.ipscan.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.IPAddress;
import com.example.ipscan.lib.PortScanReport;
import com.example.ipscan.lib.PortScanReportModel;
import com.example.ipscan.lib.port.ScanHostsRunnable;
import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.utils.ExportUtils;

import java.io.File;
import java.util.ArrayList;
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

  private PortScanReportModel portScanReportModel;
  ArrayList<String> resultData;
  private File fileForResults;

  int totalItems;
  int currentItem;
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
      if (ExportUtils.isExternalStorageWritable()) {
        Log.d(Const.LOG_TAG, "ExportUtils: DIRECTORY_DOCUMENTS: " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));

        String hostFromStr = intent.getStringExtra(Const.EXTRA_HOST_FROM);
        String hostToStr = intent.getStringExtra(Const.EXTRA_HOST_TO);
        int portFrom = intent.getIntExtra(Const.EXTRA_PORT_FROM, -1);
        int portTo = intent.getIntExtra(Const.EXTRA_PORT_TO, -1);

        Log.d(Const.LOG_TAG, "hostFrom: " + hostFromStr);
        Log.d(Const.LOG_TAG, "hostTo: " + hostToStr);
        Log.d(Const.LOG_TAG, "portFrom: " + portFrom);
        Log.d(Const.LOG_TAG, "portTo: " + portTo);

        if (hostFromStr != null && hostToStr != null && portFrom >= 0 && portTo > portFrom) {
          serviceIsBusy = true;
          startTime = System.nanoTime();

          hostFrom = new IPAddress(hostFromStr);
          hostTo = new IPAddress(hostToStr);

//          portScanReportModel = new PortScanReportModel(hostFrom, hostTo, portFrom, portTo);
//          resultData = new ArrayList<>(PortScanReport.measure(hostFrom, hostTo, portFrom, portTo));
          resultData = new ArrayList<>();
          totalItems = PortScanReport.measure(hostFrom, hostTo, portFrom, portTo);

          es.execute(new ScanHostsRunnable(hostFrom, hostTo, portFrom, portTo, Const.WAN_SOCKET_TIMEOUT,
            new PortScanResult() {
              @Override
              public <T extends Throwable> void processFinish(T err) {
                Log.e(Const.LOG_TAG, "ERROR! : " + err.toString());
              }

              @Override
              public void portWasTimedOut(String host, int portNumber) {
                currentItem++;
//                Log.d(Const.LOG_TAG, "REPORT (" + currentItem + "/" + totalItems + ") TimedOut - host: " + host + ", port: " + portNumber);
                resultData.add(PortScanReport.add(new IPAddress(host), portNumber, PortScanReport.portIsTimedOut, null));
              }

              @Override
              public void foundClosedPort(String host, int portNumber) {
                currentItem++;
//                Log.d(Const.LOG_TAG, "REPORT (" + currentItem + "/" + totalItems + ") Closed - host: " + host + ", port: " + portNumber);
                resultData.add(PortScanReport.add(new IPAddress(host), portNumber, PortScanReport.portIsClosed, null));
              }

              @Override
              public void foundOpenPort(String host, int portNumber, String banner) {
                currentItem++;
//                Log.d(Const.LOG_TAG, "REPORT (" + currentItem + "/" + totalItems + ") Open - host: " + host + ", port: " + portNumber + ", banner: " + banner);
                resultData.add(PortScanReport.add(new IPAddress(host), portNumber, PortScanReport.portIsOpen, banner));
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

                fileForResults = new File(ExportUtils.getReportsDir(),
                  ExportUtils.generateDocName(hostFromStr, hostToStr, portFrom, portTo));

//                portScanReportModel.setDuration(duration);
//                portScanReportModel.setTimeout(Const.WAN_SOCKET_TIMEOUT);
//                portScanReportModel.write(fileForResults);

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

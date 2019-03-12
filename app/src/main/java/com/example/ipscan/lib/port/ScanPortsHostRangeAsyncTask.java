package com.example.ipscan.lib.port;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ipscan.lib.IPAddress;
import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.utils.Const;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanPortsHostRangeAsyncTask extends AsyncTask<Object, Void, Void> {
  private final WeakReference<PortScanResult> delegate;

  /**
   * Constructor to set the delegate
   *
   * @param delegate Called when a port scan has finished
   */
  public ScanPortsHostRangeAsyncTask(PortScanResult delegate) {
    this.delegate = new WeakReference<>(delegate);
  }

  /**
   * Chunks the ports selected for scanning and starts the process
   * Chunked ports are scanned in parallel
   *
   * @param params IP address, start port, and stop port
   */
  @Override
  protected Void doInBackground(Object... params) {
    IPAddress ipFrom = (IPAddress) params[0];
    IPAddress ipTo = (IPAddress) params[1];
    int startPort = (int) params[2];
    int stopPort = (int) params[3];
    int timeout = (int) params[4];
    PortScanResult portScanResult = delegate.get();
    if (portScanResult != null) {
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Const.NUM_THREADS_FOR_PORT_SCAN);
      Random rand = new Random();

      int hostsCount = IPAddress.countBetween(ipFrom, ipTo);
      int threadsNumByHost = (int) Math.floor((double) Const.NUM_THREADS_FOR_PORT_SCAN/(hostsCount+1));
      int chunk = (int) Math.ceil((double) (stopPort - startPort) / threadsNumByHost);
      Log.d(Const.LOG_TAG, "ipFrom: " + ipFrom.toString());
      Log.d(Const.LOG_TAG, "ipTo: " + ipTo.toString());
      Log.d(Const.LOG_TAG, "hostsCount: " + hostsCount);
      Log.d(Const.LOG_TAG, "threadsNumByHost" + threadsNumByHost);
      Log.d(Const.LOG_TAG, "chunk" + chunk);

      for (IPAddress ipAddress = ipFrom; ipAddress.lte(ipTo); ipAddress = ipAddress.next()) {
        int previousStart = startPort;
        int previousStop = startPort + chunk;

        for (int i=0; i<threadsNumByHost; i++) {
          if (previousStop >= stopPort) {
            int schedule = rand.nextInt((int) ((((stopPort - startPort) / Const.NUM_THREADS_FOR_PORT_SCAN) / 1.5)) + 1) + 1;
            executor.schedule(new ScanPortsRunnable(ipAddress.toString(), previousStart, stopPort, timeout, delegate),  (i * hostsCount % schedule), TimeUnit.SECONDS);
//            Log.d(Const.LOG_TAG, "Execute last: ipAddress: " + ipAddress.toString() + " port from: " + previousStart + " , port to: " + stopPort + " with delay: " + (i * hostsCount % schedule));
            break;
          }

          int schedule = rand.nextInt((int) ((((stopPort - startPort) / Const.NUM_THREADS_FOR_PORT_SCAN) / 1.5)) + 1) + 1;
          executor.schedule(new ScanPortsRunnable(ipAddress.toString(), previousStart, previousStop, timeout, delegate), (i * hostsCount % schedule), TimeUnit.SECONDS);
//          Log.d(Const.LOG_TAG, "Schedule: ipAddress: " + ipAddress.toString() + " port from: " + previousStart + " , port to: " + previousStop + " with delay: " + (i * hostsCount % schedule));

          previousStart = previousStop + 1;
          previousStop = previousStop + chunk;
        }
      }

      executor.shutdown();

      try {
        executor.awaitTermination(15, TimeUnit.MINUTES);
        executor.shutdownNow();
      } catch (InterruptedException e) {
        portScanResult.processFinish(e);
      }
      portScanResult.processFinish(true);
    }

    return null;
  }
}

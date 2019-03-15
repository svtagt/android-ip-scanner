package com.example.ipscan.lib.async;

import android.util.Log;

import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.Const;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanHostsRunnable implements Runnable {
  private Host ipFrom;
  private Host ipTo;
  private int portFrom;
  private int portTo;
  private int timeout;
  private final WeakReference<PortScanResult> delegate;

  public ScanHostsRunnable(Host ipFrom, Host ipTo, int portFrom, int portTo,
                           int timeout, PortScanResult portScanResult) {
    this.ipFrom = ipFrom;
    this.ipTo = ipTo;
    this.portFrom = portFrom;
    this.portTo = portTo;
    this.timeout = timeout;
    this.delegate = new WeakReference<>(portScanResult);
  }

  @Override
  public void run() {
    PortScanResult portScanResult = delegate.get();
    if (portScanResult != null) {
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Const.NUM_THREADS_FOR_PORT_SCAN);
      Random rand = new Random();

      int hostsCount = Host.countBetween(ipFrom, ipTo);
      int threadsNumByHost = (int) Math.floor((double) Const.NUM_THREADS_FOR_PORT_SCAN/(hostsCount+1));
      int chunk = (int) Math.ceil((double) (portTo - portFrom) / threadsNumByHost);
      Log.d(Const.LOG_TAG, "ipFrom: " + ipFrom.toString());
      Log.d(Const.LOG_TAG, "ipTo: " + ipTo.toString());
      Log.d(Const.LOG_TAG, "hostsCount: " + hostsCount);
      Log.d(Const.LOG_TAG, "threadsNumByHost" + threadsNumByHost);
      Log.d(Const.LOG_TAG, "chunk" + chunk);

      for (Host host = ipFrom; host.lte(ipTo); host = host.next()) {
        int previousStart = portFrom;
        int previousStop = portFrom + chunk;

        for (int i=0; i<threadsNumByHost; i++) {
          if (previousStop >= portTo) {
            int schedule = rand.nextInt((int) ((((portTo - portFrom) / Const.NUM_THREADS_FOR_PORT_SCAN) / 1.5)) + 1) + 1;
            executor.schedule(new ScanPortsRunnable(host.toString(), previousStart, portTo, timeout, delegate),  (i * hostsCount % schedule), TimeUnit.SECONDS);
//            Log.d(Const.LOG_TAG, "Execute last: host: " + host.toString() + " port from: " + previousStart + " , port to: " + portTo + " with delay: " + (i * hostsCount % schedule));
            break;
          }

          int schedule = rand.nextInt((int) ((((portTo - portFrom) / Const.NUM_THREADS_FOR_PORT_SCAN) / 1.5)) + 1) + 1;
          executor.schedule(new ScanPortsRunnable(host.toString(), previousStart, previousStop, timeout, delegate), (i * hostsCount % schedule), TimeUnit.SECONDS);
//          Log.d(Const.LOG_TAG, "Schedule: host: " + host.toString() + " port from: " + previousStart + " , port to: " + previousStop + " with delay: " + (i * hostsCount % schedule));

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
  }
}

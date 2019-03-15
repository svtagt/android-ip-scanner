package com.example.ipscan.lib.async;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;
import com.example.ipscan.lib.result.PortScanResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanRunnable implements Runnable {
  private ArrayList<PortRange> portRangesToScan;
  private ArrayList<Host> hostsToScan;

  private int timeout;
  private final WeakReference<PortScanResult> delegate;

  private ExecutorService es;

  public ScanRunnable(ArrayList<Host> hostsToScan, ArrayList<PortRange> portRangesToScan,
                      int timeout, PortScanResult portScanResult) {
    this.hostsToScan = hostsToScan;
    this.portRangesToScan = portRangesToScan;
    this.timeout = timeout;
    this.delegate = new WeakReference<>(portScanResult);
  }

  @Override
  public void run() {
    PortScanResult portScanResult = delegate.get();
    if (portScanResult != null) {
      es = Executors.newFixedThreadPool(Const.NUM_THREADS_FOR_PORT_SCAN);
      for (int i=0; i<hostsToScan.size(); i++) {
        for (int j=0; j<portRangesToScan.size(); j++) {
          es.execute(new ScanPortRangeRunnable(hostsToScan.get(i), portRangesToScan.get(j), timeout, delegate));
        }
      }

      es.shutdown();

      try {
        es.awaitTermination(15, TimeUnit.MINUTES);
        es.shutdownNow();
      } catch (InterruptedException e) {
        portScanResult.processFinish(e);
      }
      portScanResult.processFinish(true);
    }
  }
}

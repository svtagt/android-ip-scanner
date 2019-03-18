package com.example.ipscan.lib.async;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;
import com.example.ipscan.lib.result.ScanHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanInitRunnable implements Runnable {
  private ArrayList<PortRange> portRangesToScan;
  private ArrayList<Host> hostsToScan;

  private int timeout;
  private final WeakReference<ScanHandler> delegate;

  private ExecutorService es;

  public ScanInitRunnable(ArrayList<Host> hostsToScan, ArrayList<PortRange> portRangesToScan,
                          int timeout, ScanHandler scanHandler) {
    this.hostsToScan = hostsToScan;
    this.portRangesToScan = portRangesToScan;
    this.timeout = timeout;
    this.delegate = new WeakReference<>(scanHandler);
  }

  @Override
  public void run() {
    ScanHandler scanHandler = delegate.get();
    if (scanHandler != null) {
      es = Executors.newFixedThreadPool(Const.NUM_THREADS_FOR_PORT_SCAN);
      for (int i=0; i<hostsToScan.size(); i++) {
        for (int j=0; j<portRangesToScan.size(); j++) {
          for (int k=portRangesToScan.get(j).getPortFrom(); k<portRangesToScan.get(j).getPortTo(); k++) {
            es.execute(new ScanSinglePortRunnable(hostsToScan.get(i), k, timeout, delegate));
          }
        }
      }
      es.shutdown();
      try {
        es.awaitTermination(15, TimeUnit.MINUTES);
        es.shutdownNow();
      } catch (InterruptedException e) {
        scanHandler.processFinish(e);
      }
      scanHandler.processFinish(true);
    }
  }
}

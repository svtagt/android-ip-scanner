package com.example.ipscan.core.host;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ipscan.core.IPAddress;
import com.example.ipscan.core.result.HostScanResult;
import com.example.ipscan.utils.Const;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanHostsOneHostForThreadAsyncTask extends AsyncTask<Object, Void, Void> {
  private final WeakReference<HostScanResult> delegate;

  /**
   * Constructor to set the delegate
   *
   * @param delegate Called when a single host scan has been finished
   */
  public ScanHostsOneHostForThreadAsyncTask(HostScanResult delegate) {
    this.delegate = new WeakReference<>(delegate);
  }

  /**
   * Chunks the host range selected for scanning and starts the process
   * Chunked hosts are processed in parallel
   *
   * @param params IP address start, IP address stop
   */
  @Override
  protected Void doInBackground(Object... params) {
    IPAddress ipFrom = (IPAddress) params[0];
    IPAddress ipTo = (IPAddress) params[1];

    int startPort = (int) params[2];
    int stopPort = (int) params[3];
    int timeout = (int) params[4];

    Log.d(Const.LOG_TAG, "ScanHostsAsyncTask ipFrom: " + ipFrom.toString());
    Log.d(Const.LOG_TAG, "ScanHostsAsyncTask ipTo: " + ipTo.toString());
    Log.d(Const.LOG_TAG, "ScanHostsAsyncTask startPort: " + startPort);
    Log.d(Const.LOG_TAG, "ScanHostsAsyncTask stopPort: " + stopPort);
    Log.d(Const.LOG_TAG, "ScanHostsAsyncTask timeout: " + timeout);

    HostScanResult hostScanResult = delegate.get();
    if (hostScanResult != null) {
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(IPAddress.countBetween(ipFrom, ipTo));
      int index = 1;
      for (IPAddress addressIndex = ipFrom; addressIndex.lte(ipTo); addressIndex = addressIndex.next()) {
        Log.d(Const.LOG_TAG, "IP to execute: " + addressIndex.toString());
        executor.schedule(new ScanHostsOneHostForThreadRunnable(addressIndex, startPort, stopPort, timeout, delegate), index*5, TimeUnit.SECONDS);
        index++;
      }

      executor.shutdown();

      try {
        executor.awaitTermination(5, TimeUnit.MINUTES);
        executor.shutdownNow();
      } catch (InterruptedException e) {
        hostScanResult.processFinish(e);
      }

//      hostScanResult.processFinish("aaaaaaaa finish", true);
    }

    return null;
  }
}

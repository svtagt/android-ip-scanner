package com.example.ipscan.core.host;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ipscan.core.IPAddress;
import com.example.ipscan.core.result.HostScanResult;
import com.example.ipscan.utils.Const;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanHostsAsyncTask extends AsyncTask<Object, Void, Void> {
  private final WeakReference<HostScanResult> delegate;

  /**
   * Constructor to set the delegate
   *
   * @param delegate Called when a single host scan has been finished
   */
  public ScanHostsAsyncTask(HostScanResult delegate) {
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
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Const.NUM_THREADS_FOR_IP_SCAN);
      Random rand = new Random();

      int chunk = (int) Math.ceil((double) (IPAddress.countBetween(ipFrom, ipTo)) / Const.NUM_THREADS_FOR_IP_SCAN);
      Log.d(Const.LOG_TAG, "ScanHostsAsyncTask chunk: " + chunk);

      IPAddress prevStartIp = ipFrom;
      IPAddress prevStopIp = ipFrom.next(chunk);

      Log.d(Const.LOG_TAG, "ScanHostsAsyncTask prevStartIp: " + prevStartIp.toString());
      Log.d(Const.LOG_TAG, "ScanHostsAsyncTask prevStopIp: " + prevStopIp.toString());

      for (int i = 0; i < Const.NUM_THREADS_FOR_IP_SCAN; i++) {
        if (prevStopIp.gte(ipTo)) {
          executor.execute(new ScanHostsRunnable(prevStartIp, ipTo, startPort, stopPort, timeout, delegate));
          break;
        }

        int schedule = rand.nextInt((int) ((((IPAddress.countBetween(ipFrom, ipTo)) / Const.NUM_THREADS_FOR_IP_SCAN) / 1.5)) + 1) + 1;

        executor.schedule(new ScanHostsRunnable(prevStartIp, prevStopIp, startPort, stopPort, timeout, delegate), i % schedule, TimeUnit.SECONDS);

        prevStartIp = prevStopIp.next();
        prevStopIp = prevStopIp.next(chunk);

        Log.d(Const.LOG_TAG, "ScanHostsAsyncTask prevStartIp next: " + prevStartIp.toString());
        Log.d(Const.LOG_TAG, "ScanHostsAsyncTask prevStopIp next: " + prevStopIp.toString());
      }

      executor.shutdown();

      try {
        executor.awaitTermination(5, TimeUnit.MINUTES);
        executor.shutdownNow();
      } catch (InterruptedException e) {
        hostScanResult.processFinish(e);
      }

      hostScanResult.processFinish("aaaaaaaa finish", true);
    }

    return null;
  }
}

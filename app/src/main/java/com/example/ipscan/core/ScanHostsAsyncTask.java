package com.example.ipscan.core;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ipscan.utils.Const;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanHostsAsyncTask extends AsyncTask<Object, Void, Void> {
  private final WeakReference<HostAsyncResponse> delegate;

  /**
   * Constructor to set the delegate
   *
   * @param delegate Called when a single host scan has been finished
   */
  public ScanHostsAsyncTask(HostAsyncResponse delegate){
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
    int timeout = (int) params[2];

    Log.d(Const.LOG_TAG, "ipFrom: " + ipFrom.toString());
    Log.d(Const.LOG_TAG, "ipTo: " + ipFrom.toString());

    HostAsyncResponse hostAsyncResponse = delegate.get();
    if (hostAsyncResponse != null) {
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Const.NUM_THREADS_FOR_IP_SCAN);
      Random rand = new Random();

      int chunk = (int) Math.ceil((double) (IPAddress.countBetween(ipFrom, ipTo)) / Const.NUM_THREADS_FOR_IP_SCAN);
      Log.d(Const.LOG_TAG, "chunk: " + chunk);

      IPAddress prevStartIp = ipFrom;
      IPAddress prevStopIp = ipFrom.next(chunk);

      Log.d(Const.LOG_TAG, "prevStartIp: " + prevStartIp.toString());
      Log.d(Const.LOG_TAG, "prevStopIp: " + prevStopIp.toString());

      for (int i = 0; i < Const.NUM_THREADS_FOR_IP_SCAN; i++) {
        if (prevStopIp.gte(ipTo)) {
//          executor.execute(new ScanHostsRunnable(prevStopIp, ipTo, timeout, delegate));
          break;
        }

        int schedule = rand.nextInt((int) ((((IPAddress.countBetween(ipFrom, ipTo)) / Const.NUM_THREADS_FOR_IP_SCAN) / 1.5)) + 1) + 1;
        Log.d(Const.LOG_TAG, "schedule: " + schedule);
//        executor.schedule(new ScanHostsRunnable(prevStartIp, prevStopIp, timeout, delegate), i % schedule, TimeUnit.SECONDS);

        prevStartIp = prevStopIp.next();
        prevStopIp = prevStopIp.next(chunk);
      }


      executor.shutdown();

      try {
        executor.awaitTermination(5, TimeUnit.MINUTES);
        executor.shutdownNow();
      } catch (InterruptedException e) {
        hostAsyncResponse.processFinish(e);
      }

      hostAsyncResponse.processFinish(true);
    }

    return null;
  }
}

package com.example.ipscan.core;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ipscan.utils.Const;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScanPortsAsyncTask extends AsyncTask<Object, Void, Void> {
  private final WeakReference<HostAsyncResponse> delegate;

  /**
   * Constructor to set the delegate
   *
   * @param delegate Called when a port scan has finished
   */
  public ScanPortsAsyncTask(HostAsyncResponse delegate) {
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
    String ip = (String) params[0];
    int startPort = (int) params[1];
    int stopPort = (int) params[2];
    int timeout = (int) params[3];
    HostAsyncResponse hostAsyncResponse = delegate.get();
    if (hostAsyncResponse != null) {
      try {
        InetAddress address = InetAddress.getByName(ip);
        ip = address.getHostAddress();
      } catch (UnknownHostException e) {
        hostAsyncResponse.processFinish(false);
        hostAsyncResponse.processFinish(e);

        return null;
      }

      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Const.NUM_THREADS_FOR_PORT_SCAN);
      Random rand = new Random();

      int chunk = (int) Math.ceil((double) (stopPort - startPort) / Const.NUM_THREADS_FOR_PORT_SCAN);
      int previousStart = startPort;
      int previousStop = startPort + chunk;

      for (int i = 0; i < Const.NUM_THREADS_FOR_PORT_SCAN; i++) {
        if (previousStop >= stopPort) {
          executor.execute(new ScanPortsRunnable(ip, previousStart, stopPort, timeout, delegate));
          break;
        }

        int schedule = rand.nextInt((int) ((((stopPort - startPort) / Const.NUM_THREADS_FOR_PORT_SCAN) / 1.5)) + 1) + 1;
        Log.d(Const.LOG_TAG, "schedule: " + schedule);
        executor.schedule(new ScanPortsRunnable(ip, previousStart, previousStop, timeout, delegate), i % schedule, TimeUnit.SECONDS);

        previousStart = previousStop + 1;
        previousStop = previousStop + chunk;
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

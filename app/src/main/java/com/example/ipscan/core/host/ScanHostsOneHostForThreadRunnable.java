package com.example.ipscan.core.host;

import android.util.Log;
import android.util.SparseArray;

import com.example.ipscan.core.HostModel;
import com.example.ipscan.core.IPAddress;
import com.example.ipscan.core.result.HostScanResult;
import com.example.ipscan.core.result.PortScanResult;
import com.example.ipscan.utils.Const;

import java.lang.ref.WeakReference;

public class ScanHostsOneHostForThreadRunnable implements Runnable {
  private IPAddress ip;

  private int startPort;
  private int stopPort;
  private int timeout;

  private final WeakReference<HostScanResult> delegate;

  /**
   * Constructor to set the necessary data to perform a port scan
   *
   * @param ip        IP
   * @param startPort The port to start scanning at
   * @param stopPort  The port to stop scanning at
   * @param timeout   Socket timeout
   * @param delegate  Called when this chunk of ports has finished scanning
   */
  public ScanHostsOneHostForThreadRunnable(IPAddress ip, int startPort, int stopPort, int timeout, WeakReference<HostScanResult> delegate) {
    this.ip = ip;

    this.startPort = startPort;
    this.stopPort = stopPort;
    this.timeout = timeout;

    this.delegate = delegate;
  }

  /**
   * Starts the host scan
   */
  @Override
  public void run() {
    Log.d(Const.LOG_TAG, Thread.currentThread().getName() + " started with IP: " + ip.toString());
    HostScanResult hostScanResult = delegate.get();

    HostModel.scanPorts(ip.toString(), startPort, stopPort, timeout, new PortScanResult() {
      @Override
      public <T extends Throwable> void processFinish(T output) {

      }

      @Override
      public void processFinish(String ip, int output) {
//        Log.d(Const.LOG_TAG, "open port for ip: " + ip + " port number: " + output);
      }

      @Override
      public void processFinish(String ip, boolean output) {
        Log.d(Const.LOG_TAG, "finish scanning hosts of ip: " + ip);
      }

      @Override
      public void processFinish(String ip, SparseArray<String> output) {
        int scannedPort = output.keyAt(0);
        String item = String.valueOf(scannedPort);

        Log.d(Const.LOG_TAG, "OPEN PORT ip: " + ip + ", scannedPort: " + scannedPort);
      }
    });

  }
}

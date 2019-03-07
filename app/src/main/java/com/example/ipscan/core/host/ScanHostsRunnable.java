package com.example.ipscan.core.host;

import android.util.Log;
import android.util.SparseArray;

import com.example.ipscan.core.HostModel;
import com.example.ipscan.core.IPAddress;
import com.example.ipscan.core.port.PortScanResult;
import com.example.ipscan.utils.Const;

import java.lang.ref.WeakReference;

public class ScanHostsRunnable implements Runnable {
  private IPAddress ipFrom;
  private IPAddress ipTo;

  private int startPort;
  private int stopPort;
  private int timeout;

  private final WeakReference<PortScanResult> delegate;

  /**
   * Constructor to set the necessary data to perform a port scan
   *
   * @param ipFrom    Port to start scanning at
   * @param ipTo      Port to stop scanning at
   * @param startPort The port to start scanning at
   * @param stopPort  The port to stop scanning at
   * @param timeout   Socket timeout
   * @param delegate  Called when this chunk of ports has finished scanning
   */
  public ScanHostsRunnable(IPAddress ipFrom, IPAddress ipTo, int startPort, int stopPort, int timeout, WeakReference<PortScanResult> delegate) {
    this.ipFrom = ipFrom;
    this.ipTo = ipTo;

    this.startPort = startPort;
    this.stopPort = stopPort;
    this.timeout = timeout;

    this.delegate = delegate;
  }

  /**
   * Starts the port scan
   */
  @Override
  public void run() {
    PortScanResult hostAsyncResponse = delegate.get();
    PortScanResult portRes = new PortScanResult() {
      @Override
      public <T extends Throwable> void processFinish(T output) {

      }

      @Override
      public void processFinish(String ip, int output) {

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
    };


    for (IPAddress addressIndex = ipFrom; addressIndex.lte(ipTo); addressIndex = addressIndex.next()) {
      if (hostAsyncResponse == null) {
        return;
      }

      Log.d(Const.LOG_TAG, "ScanHostsRunnable addressIndex: " + addressIndex);
      HostModel.scanPorts(addressIndex.toString(), startPort, stopPort, timeout, portRes);
    }

    hostAsyncResponse.processFinish("aaa finish", true);
  }
}

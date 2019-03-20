package com.example.ipscan.lib.async;

import android.util.SparseArray;

import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.result.ScanHandler;
import com.example.ipscan.lib.applied.ScannerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;

public class ScanSingleHostPortRunnable implements Runnable {
  private Host host;
  private int port;
  private int timeout;
  private final WeakReference<ScanHandler> delegate;

  /**
   * Constructor to set the necessary data to perform a port scan
   *
   * @param host      IP address
   * @param port      Port to start scanning at
   * @param timeout   Socket timeout
   * @param delegate  Called when this chunk of ports has finished scanning
   */
  public ScanSingleHostPortRunnable(Host host, int port, int timeout, WeakReference<ScanHandler> delegate) {
    this.host = host;
    this.port = port;
    this.timeout = timeout;
    this.delegate = delegate;
  }

  /**
   * Starts the port scan
   */
  @Override
  public void run() {
    ScanHandler scanHandler = delegate.get();
    if (scanHandler == null) {
      return;
    }
    Socket socket = new Socket();
    try {
      socket.setReuseAddress(true);
      socket.setTcpNoDelay(true);
      socket.connect(new InetSocketAddress(this.host.toString(), port), timeout);
    } catch (IllegalBlockingModeException | IllegalArgumentException e) {
      scanHandler.processFinish(e);
      return;
    } catch (SocketTimeoutException e) {
      scanHandler.portWasTimedOut(this.host, port);
      scanHandler.processItem();
      return;
    } catch (IOException e) {
      scanHandler.foundClosedPort(this.host, port);
      scanHandler.processItem();
      return;
    }

    SparseArray<String> portData = new SparseArray<>();
    String data = null;
    try {
      InputStreamReader input = new InputStreamReader(socket.getInputStream(), "UTF-8");
      BufferedReader buffered = new BufferedReader(input);
      if (port == 22) {
        data = ScannerUtils.parseSSH(buffered);
      } else if (port == 80 || port == 443 || port == 8080) {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        data = ScannerUtils.parseHTTP(host.toString(), buffered, out);
      }
    } catch (IOException e) {
      scanHandler.processFinish(e);
    } finally {
      portData.put(port, data);
      scanHandler.foundOpenPort(this.host, port, data);
      scanHandler.processItem();
      try {
        socket.close();
      } catch (IOException e) {
        // Something's really wrong if we can't close the socket...
        scanHandler.processFinish(e);
      }
    }
  }
}

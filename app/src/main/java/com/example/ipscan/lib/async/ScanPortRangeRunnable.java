package com.example.ipscan.lib.async;

import android.util.SparseArray;

import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.helpers.PortRange;
import com.example.ipscan.lib.result.PortScanResult;

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

public class ScanPortRangeRunnable implements Runnable {
  private Host host;
  private PortRange portRange;
  private int timeout;
  private final WeakReference<PortScanResult> delegate;

  /**
   * Constructor to set the necessary data to perform a port scan
   *
   * @param host       IP address
   * @param portRange Port to start scanning at
   * @param timeout   Socket timeout
   * @param delegate  Called when this chunk of ports has finished scanning
   */
  public ScanPortRangeRunnable(Host host, PortRange portRange, int timeout, WeakReference<PortScanResult> delegate) {
    this.host = host;
    this.portRange = portRange;
    this.timeout = timeout;
    this.delegate = delegate;
  }

  /**
   * Starts the port scan
   */
  @Override
  public void run() {
    PortScanResult portScanResult = delegate.get();
    if (portScanResult == null) {
      return;
    }

    int startPort = this.portRange.getPortFrom();
    int stopPort = this.portRange.getPortTo();

    for (int i = startPort; i <= stopPort; i++) {

      Socket socket = new Socket();
      try {
        socket.setReuseAddress(true);
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(this.host.toString(), i), timeout);
      } catch (IllegalBlockingModeException | IllegalArgumentException e) {
        portScanResult.processFinish(e);
        continue;
      } catch (SocketTimeoutException e) {
        portScanResult.portWasTimedOut(this.host.toString(), i);
        portScanResult.processItem();
        continue;
      } catch (IOException e) {
        portScanResult.foundClosedPort(this.host.toString(), i);
        portScanResult.processItem();
        continue; // Connection failures mean that the port isn't open.
      }


      SparseArray<String> portData = new SparseArray<>();
      String data = null;
      try {
        InputStreamReader input = new InputStreamReader(socket.getInputStream(), "UTF-8");
        BufferedReader buffered = new BufferedReader(input);
        if (i == 22) {
          data = parseSSH(buffered);
        } else if (i == 80 || i == 443 || i == 8080) {
          PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
          data = parseHTTP(host.toString(), buffered, out);

        }
      } catch (IOException e) {
        portScanResult.processFinish(e);
      } finally {
        portData.put(i, data);
        portScanResult.foundOpenPort(this.host.toString(), i, data);
        portScanResult.processItem();
        try {
          socket.close();
        } catch (IOException ignored) {
          // Something's really wrong if we can't close the socket...
        }
      }
    }
  }

  /**
   * Tries to determine the SSH version used.
   *
   * @param reader Reads SSH version from the connected socket
   * @return SSH banner
   * @throws IOException
   */
  private String parseSSH(BufferedReader reader) throws IOException {
    try {
      return reader.readLine();
    } finally {
      reader.close();
    }
  }

  /**
   * Tries to determine what web server is used
   *
   * @param reader Reads headers to determine server type
   * @param writer Sends HTTP request to get a response to parse
   * @return HTTP banner
   * @throws IOException
   */
  private String parseHTTP(String host, BufferedReader reader, PrintWriter writer) throws IOException {
    writer.println("GET / HTTP/1.1\r\nHost: " + host + "\r\n");
    char[] buffer = new char[256];
    reader.read(buffer, 0, buffer.length);
    writer.close();
    reader.close();
    String data = new String(buffer).toLowerCase();

    if (data.contains("apache") || data.contains("httpd")) {
      return "Apache";
    }

    if (data.contains("iis") || data.contains("microsoft")) {
      return "IIS";
    }

    if (data.contains("nginx")) {
      return "NGINX";
    }

    return null;
  }
}

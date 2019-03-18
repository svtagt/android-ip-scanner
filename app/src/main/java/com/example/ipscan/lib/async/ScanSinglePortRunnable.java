package com.example.ipscan.lib.async;

import android.util.SparseArray;

import com.example.ipscan.lib.helpers.Host;
import com.example.ipscan.lib.result.ScanHandler;

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

public class ScanSinglePortRunnable implements Runnable {
  private Host host;
  private int port;
  private int timeout;
  private final WeakReference<ScanHandler> delegate;

  /**
   * Constructor to set the necessary data to perform a port scan
   *
   * @param host       IP address
   * @param port Port to start scanning at
   * @param timeout   Socket timeout
   * @param delegate  Called when this chunk of ports has finished scanning
   */
  public ScanSinglePortRunnable(Host host, int port, int timeout, WeakReference<ScanHandler> delegate) {
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
        data = parseSSH(buffered);
      } else if (port == 80 || port == 443 || port == 8080) {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        data = parseHTTP(host.toString(), buffered, out);

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

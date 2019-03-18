package com.example.ipscan.lib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpUtils {
  /**
   * Tries to determine the SSH version used.
   *
   * @param reader Reads SSH version from the connected socket
   * @return SSH banner
   * @throws IOException
   */
  public static String parseSSH(BufferedReader reader) throws IOException {
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
  public static String parseHTTP(String host, BufferedReader reader, PrintWriter writer) throws IOException {
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

package com.example.ipscan.lib;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PortScanReport {
  public static final String portIsClosed = "CLOSED";
  public static final String portIsOpen = "OPEN";
  public static final String portIsTimedOut = "TIMED_OUT";

  public static String[] init(IPAddress hostFrom, IPAddress hostTo, int portFrom, int portTo) {
    int portOffset = portTo - portFrom + 1;
    int hostsRangeSize = IPAddress.range(hostFrom, hostTo);
    int reportArraySize = portOffset * hostsRangeSize;

    Log.d(Const.LOG_TAG, "reportArraySize: " + reportArraySize);
    return new String[reportArraySize];
  }

  public static int measure(IPAddress hostFrom, IPAddress hostTo, int portFrom, int portTo) {
    int portOffset = portTo - portFrom + 1;
    int hostsRangeSize = IPAddress.range(hostFrom, hostTo);
    int reportArraySize = portOffset * hostsRangeSize;

    Log.d(Const.LOG_TAG, "reportArraySize: " + reportArraySize);
    return reportArraySize;
  }


  public static String add(IPAddress host, int port, String status, String banner) {
    StringBuilder sb = new StringBuilder();
    sb.append(host);
    sb.append(";");
    sb.append(port);
    sb.append(";");
    sb.append(status);
    sb.append(";");

    if (banner != null) {
      sb.append(banner);
      sb.append(";");
    }
    sb.append('\n');

    return sb.toString();
  }

  public static File write(ArrayList<String> resultData, File file) {
    try (PrintWriter printWriter = new PrintWriter(file)) {
      Log.d(Const.LOG_TAG, "Begin writing to file...");
      StringBuilder sb = new StringBuilder();
      for (int i=0; i<resultData.size(); i++) {
        sb.append(resultData.get(i));
      }

      printWriter.write(sb.toString());
      printWriter.close();

    } catch (FileNotFoundException e) {
      Log.e(Const.LOG_TAG, e.getMessage());
    }

    Log.d(Const.LOG_TAG, "Writing was finished");

    return file;
  }
}

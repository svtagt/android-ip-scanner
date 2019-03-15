package com.example.ipscan.lib.helpers;

import android.util.Log;

import com.example.ipscan.lib.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PortScanReport {
  public static final String portIsClosed = "closed";
  public static final String portIsOpen = "open";
  public static final String portIsTimedOut = "timed_out";

  public static String[] init(Host hostFrom, Host hostTo, int portFrom, int portTo) {
    int portOffset = portTo - portFrom + 1;
    int hostsRangeSize = Host.range(hostFrom, hostTo);
    int reportArraySize = portOffset * hostsRangeSize;

    Log.d(Const.LOG_TAG, "reportArraySize: " + reportArraySize);
    return new String[reportArraySize];
  }

  public static long measure(ArrayList<Host> hostsToScan, ArrayList<PortRange> portRangesToScan) {

    int portsCount = 0;
    for (int i=0; i<portRangesToScan.size(); i++) {
      portRangesToScan.get(i).print();
      Log.e(Const.LOG_TAG, "!!!" + portRangesToScan.get(i).length());
      portsCount+=portRangesToScan.get(i).length();
    }
    System.out.println("portsCount: " + portsCount);
    return hostsToScan.size() * portsCount;
  }


  public static String add(Host host, int port, String status, String banner) {
//    StringBuilder sb = new StringBuilder();
//    sb.append(host);
//    sb.append(";");
//    sb.append(port);
//    sb.append(";");
//    sb.append(status);
//    sb.append(";");
//
//    if (banner != null) {
//      sb.append(banner);
//      sb.append(";");
//    }
//    sb.append('\n');
//
//    return sb.toString();

    return host + ";" + port + ";" + status + (banner != null ? (";" + banner + ";\n") : ";\n");
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

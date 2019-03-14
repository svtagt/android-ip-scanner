package com.example.ipscan.lib;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PortScanReportModel {
  public static final String portIsClosed = "CLOSED";
  public static final String portIsTimedOut = "TIMED_OUT";
  public static final String noBanner = "<OPEN_BUT_NO_BANNER>";

  private String[][] data;

  private int portOffset;
  private int hostsRangeSize;

  private int portFrom;
  private int portTo;
  private IPAddress hostFrom;
  private IPAddress hostTo;

  private int timeout;
  private long duration;

  public PortScanReportModel(IPAddress hostFrom, IPAddress hostTo, int portFrom, int portTo) {
    this.portFrom = portFrom;
    this.portTo = portTo;

    this.portOffset = portTo - portFrom + 1;
    this.hostsRangeSize = IPAddress.range(hostFrom, hostTo);

    this.hostFrom = hostFrom;
    this.hostTo = hostTo;

    this.data = new String[portOffset][hostsRangeSize];
    for (int i=0; i<portOffset; i++){
      data[i] = new String[hostsRangeSize];
    }
  }

  private void set(IPAddress host, int port, String value) {
    int indexOfPort = port - portFrom;
    int indexOfHost = IPAddress.countBetween(this.hostFrom, host);
    this.data[indexOfPort][indexOfHost] = value;
  }

  public void markAsClosed(IPAddress host, int port) {
    this.set(host, port, this.portIsClosed);
  }

  public void markAsTimedOut(IPAddress host, int port) {
    this.set(host, port, this.portIsTimedOut);
  }

  public void markAsOpen(IPAddress host, int port, String banner) {
    String bannerStr = banner != null ? banner : this.noBanner;
    this.set(host, port, bannerStr);
  }

  private int getPortNumber(int port) {
    return port + this.portFrom;
  }

  private String getHostStrByIndex(int index) {
    return hostFrom.next(index).toString();
  }

  public File write(File file) {
    try (PrintWriter printWriter = new PrintWriter(file)) {
      printWriter.write(makeHeader().toString());
      for (int i=0; i<this.data.length; i++) {
        StringBuilder sb = new StringBuilder();
        sb.append(getPortNumber(i));
        sb.append(';');

        for (int j=0; j<this.data[i].length; j++) {
          sb.append(this.data[i][j]);
          sb.append(';');
        }
        sb.append('\n');
        printWriter.write(sb.toString());
      }
      printWriter.close();
    } catch (FileNotFoundException e) {
      Log.e(Const.LOG_TAG, e.getMessage());
    }

    Log.d(Const.LOG_TAG, "Writing was finished");
    return file;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  private StringBuilder makeHeader() {
    StringBuilder sb = new StringBuilder();
    if (timeout > 0) {
      sb.append("Timeout: ");
      sb.append(";");
      sb.append(timeout);
      sb.append(";");
    }
    if (duration > 0) {
      sb.append("Duration: ");
      sb.append(";");
      sb.append(duration);
      sb.append(";");
      sb.append("ms");
      sb.append(";");
    }

    if (sb.length() > 0) {
      sb.append('\n');
    }

    sb.append("Port #;");
    for (IPAddress ipAddress = hostFrom; ipAddress.lte(hostTo); ipAddress = ipAddress.next()) {
      sb.append(ipAddress.toString());
      sb.append(";");
    }
    sb.append('\n');
    return sb;
  }
}

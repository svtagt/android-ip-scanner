package com.example.ipscan.lib;

import android.util.Log;

import com.example.ipscan.lib.helpers.Host;

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
  private Host hostFrom;
  private Host hostTo;

  private int timeout;
  private long duration;

  public PortScanReportModel(Host hostFrom, Host hostTo, int portFrom, int portTo) {
    this.portFrom = portFrom;
    this.portTo = portTo;

    this.portOffset = portTo - portFrom + 1;
    this.hostsRangeSize = Host.range(hostFrom, hostTo);

    this.hostFrom = hostFrom;
    this.hostTo = hostTo;

    this.data = new String[portOffset][hostsRangeSize];
    for (int i=0; i<portOffset; i++){
      data[i] = new String[hostsRangeSize];
    }
  }

  private void set(Host host, int port, String value) {
    int indexOfPort = port - portFrom;
    int indexOfHost = Host.countBetween(this.hostFrom, host);
    this.data[indexOfPort][indexOfHost] = value;
  }

  public void markAsClosed(Host host, int port) {
    this.set(host, port, this.portIsClosed);
  }

  public void markAsTimedOut(Host host, int port) {
    this.set(host, port, this.portIsTimedOut);
  }

  public void markAsOpen(Host host, int port, String banner) {
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
    for (Host host = hostFrom; host.lte(hostTo); host = host.next()) {
      sb.append(host.toString());
      sb.append(";");
    }
    sb.append('\n');
    return sb;
  }
}

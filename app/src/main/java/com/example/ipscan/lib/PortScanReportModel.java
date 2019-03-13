package com.example.ipscan.lib;

import android.util.Log;

public class PortScanReportModel {
  public static final String portIsClosed = "PORT_IS_CLOSED";
  public static final String portIsTimedOut = "CONNECTION_TIMED_OUT";

  private String[][] data;

  private int portOffset;
  private int hostsRangeSize;

  private int portFrom;
  private int portTo;
  private IPAddress hostFrom;
  private IPAddress hostTo;

  public PortScanReportModel(IPAddress hostFrom, IPAddress hostTo, int portFrom, int portTo) {
    this.portFrom = portFrom;
    this.portTo = portTo;

    this.portOffset = portTo - portFrom + 1; //1003
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
    Log.d(Const.LOG_TAG, "SET host: " + host.toString() + " port: " + port + " indexOfPort: " + indexOfPort + " indexOfHost: " + indexOfHost);
    this.data[indexOfPort][indexOfHost] = value;
  }

  public void markAsClosed(IPAddress host, int port) {
    this.set(host, port, this.portIsClosed);
  }

  public void markAsTimedOut(IPAddress host, int port) {
    this.set(host, port, this.portIsTimedOut);
  }

  public void markAsOpen(IPAddress host, int port, String banner) {
    this.set(host, port, banner);
  }

  public void print() {
    for (int i=0; i<this.data.length; i++) {
      String str = "PORT " + getPortNumber(i) + " ";
      for (int j=0; j<this.data[i].length; j++) {
        str = str + " host:" + getHostStrByIndex(j) + "/val:" + this.data[i][j];
      }
//      Log.d(Const.LOG_TAG, str);
      System.out.println(str);
    }
  }

  private int getPortNumber(int port) {
    return port + this.portFrom;
  }

  private String getHostStrByIndex(int index) {
    return hostFrom.next(index).toString();
  }
}

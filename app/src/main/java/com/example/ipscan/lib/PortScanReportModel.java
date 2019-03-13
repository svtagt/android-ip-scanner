package com.example.ipscan.lib;

import android.util.Log;

import java.util.ArrayList;

public class PortScanReportModel {
  public static final String portIsClosed = "PORT_IS_CLOSED";
  public static final String portIsTimedOut = "CONNECTION_TIMED_OUT";


  private ArrayList<ArrayList<String>> data;
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

    this.data = new ArrayList<>(portOffset);
    for (int i = 0; i < portOffset; i++) {
      this.data.add(new ArrayList<>(hostsRangeSize));
    }
  }

  private void set(IPAddress host, int port, String value) {
    int indexOfPort = port - portFrom;
    int indexOfHost = IPAddress.countBetween(this.hostFrom, host);
    Log.d(Const.LOG_TAG, "SET host: " + host.toString() + " port: " + port + " indexOfPort: " + indexOfPort + " indexOfHost: " + indexOfHost);
    this.data.get(indexOfPort).set(indexOfHost, value);
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
    for (int i=0; i<this.data.size(); i++) {
      String str = "PORT " + getPortNumber(i) + " ";
      ArrayList<String> portData = this.data.get(i);
      for (int j=0; j<portData.size(); i++) {
        str = str + "host:" + getHostStrByIndex(j) + "/val:" + portData.get(j);
      }
      Log.d(Const.LOG_TAG, str);
    }
  }

  private int getPortNumber(int port) {
    return port + this.portOffset;
  }

  private String getHostStrByIndex(int index) {
    return hostFrom.next(index).toString();
  }
}

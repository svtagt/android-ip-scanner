package com.example.ipscan.lib.helpers;

public class PortRange {
  private int portFrom;
  private int portTo;

  public PortRange(int portFrom, int portTo) {
    this.portFrom = portFrom;
    this.portTo = portTo;
  }

  public PortRange(int port) {
    this.portFrom = port;
    this.portTo = port;
  }

  public int getPortFrom() {
    return portFrom;
  }

  public void setPortFrom(int portFrom) {
    this.portFrom = portFrom;
  }

  public int getPortTo() {
    return portTo;
  }

  public void setPortTo(int portTo) {
    this.portTo = portTo;
  }


}

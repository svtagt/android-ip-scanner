package com.example.ipscan.lib.helpers;

import android.util.Log;

import com.example.ipscan.lib.Const;

public class PortRange {
  private int portFrom;
  private int portTo;

  public PortRange(int portFrom, int portTo) {
    this.portFrom = portFrom;
    this.portTo = portTo;
  }

  public int getPortFrom() {
    return portFrom;
  }

  public int getPortTo() {
    return portTo;
  }

  public int length() {
    if (portFrom == portTo) {
      return 1;
    }
    return portTo - portFrom;
  }

  public void print() {
    Log.d(Const.LOG_TAG, "portFrom: " + portFrom + " portTo: " + portFrom);
  }
}

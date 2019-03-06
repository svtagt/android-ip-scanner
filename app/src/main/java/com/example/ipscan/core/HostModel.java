package com.example.ipscan.core;

import android.util.Log;

import com.example.ipscan.utils.Const;

import java.io.Serializable;

public class HostModel implements Serializable {

  private String hostname;
  private String ip;
  private String mac;

  /**
   * Constructs a host with a known IP and MAC.
   *
   * @param ip
   * @param mac
   */
  public HostModel(String ip, String mac) {
    this.ip = ip;
    this.mac = mac;
  }

  /**
   * Returns this host's hostname
   *
   * @return
   */
  public String getHostname() {
    return hostname;
  }

  /**
   * Sets this host's hostname to the given value
   *
   * @param hostname Hostname for this host
   * @return
   */
  public HostModel setHostname(String hostname) {
    this.hostname = hostname;
    return this;
  }

  /**
   * Returns this host's IP address
   *
   * @return
   */
  public String getIp() {
    return ip;
  }

  /**
   * Returns this host's MAC address
   *
   * @return
   */
  public String getMac() {
    return mac;
  }

  //TODO use IPAddress instead string
  /**
   * Starts a port scan
   *
   * @param ip        IP address
   * @param startPort The port to start scanning at
   * @param stopPort  The port to stop scanning at
   * @param timeout   Socket timeout
   * @param delegate  Delegate to be called when the port scan has finished
   */
  public static void scanPorts(String ip, int startPort, int stopPort, int timeout, HostAsyncResponse delegate) {
    new ScanPortsAsyncTask(delegate).execute(ip, startPort, stopPort, timeout);
  }

  /**
   * Starts a hosts scan
   *
   * @param ipFrom The port to start scanning at
   * @param ipTo  The port to stop scanning at
   * @param delegate  Delegate to be called when the port scan has finished
   */
  public static void scanHosts(IPAddress ipFrom, IPAddress ipTo, int startPort, int stopPort, int timeout, HostAsyncResponse delegate) {
    Log.d(Const.LOG_TAG, "ipFrom: " + ipFrom);
    Log.d(Const.LOG_TAG, "ipTo: " + ipTo);
    Log.d(Const.LOG_TAG, "startPort: " + startPort);
    Log.d(Const.LOG_TAG, "stopPort: " + stopPort);
    Log.d(Const.LOG_TAG, "timeout: " + timeout);
    new ScanHostsAsyncTask(delegate).execute(ipFrom, ipTo, startPort, stopPort, timeout);
  }
}
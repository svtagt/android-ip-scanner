package com.example.ipscan.lib.utils;

public class ParamsParser {
  private static final String PORTS_ARG = "-p";
  private static final String HOSTS_ARG = "-h";
  
  public static final String TYPE_PORTS = "TYPE_PORTS";
  public static final String TYPE_HOSTS = "TYPE_HOSTS";

  public static String getPortsStr(String params) {
    int pIndex = params.indexOf(PORTS_ARG);
    if (pIndex == -1) {
      return "";
    }
    int portsSubsBeginIndex = pIndex + PORTS_ARG.length(); 

    String portsStr = params.substring(portsSubsBeginIndex).trim();
    if (portsStr.contains(HOSTS_ARG)) {
      int hIndex = portsStr.indexOf(HOSTS_ARG);
      portsStr = portsStr.substring(0, hIndex).trim();
    }

    return portsStr;
  }

  public static String getHostsStr(String params) {
    return "";
  }

  public static boolean[] makePortsToScanArray(String portsStr) {
    return new boolean[65536];
  }
}

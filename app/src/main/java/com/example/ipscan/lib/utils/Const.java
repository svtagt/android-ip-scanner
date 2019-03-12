package com.example.ipscan.lib.utils;

public class Const {
  public static final String LOG_TAG = "IPSCAN";

  public static final String EXTRA_HOST_FROM = "hostFrom";
  public static final String EXTRA_HOST_TO = "hostTo";
  public static final String EXTRA_PORT_FROM = "portFrom";
  public static final String EXTRA_PORT_TO = "portTo";

  public static final int MAX_PORT_VALUE = 65535;
  public static final int MIN_PORT_VALUE = 1;
  public static final int WAN_SOCKET_TIMEOUT = 12000;

  //TODO: don't use fixed value
  public static final int NUM_THREADS_FOR_PORT_SCAN = 1000;
  public static final int NUM_THREADS_FOR_IP_SCAN = 2;
}
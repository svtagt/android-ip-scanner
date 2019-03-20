package com.example.ipscan.lib;

public class Const {
  public static final String LOG_TAG = "IPSCAN_LIB";

  public static final String EXTRA_SCAN_PARAMS = "hostFrom";
  public static final String REPORTS_DIR_NAME = "IPScanReports";
  public static final String ACTION_START_NETWORK_SERVICE = "ACTION_START_NETWORK_SERVICE";
  public static final String ACTION_SET_NETWORK_SERVICE_ALARM = "ACTION_SET_NETWORK_SERVICE_ALARM";

  public static final int MAX_PORT_VALUE = 65535;
  public static final int MIN_PORT_VALUE = 1;
  public static final int WAN_SOCKET_TIMEOUT = 12000;

  //TODO: don't use fixed value
  public static final int NUM_THREADS_FOR_PORT_SCAN = 500;
  public static final int ONGOING_NOTIFICATION_ID = 1;
  public static final String CHANNEL_ID = "IPSCAN_CHANNEL_ID";

  public static final String API_PREFIX = "https://r.skaro.icu/scanner";
}
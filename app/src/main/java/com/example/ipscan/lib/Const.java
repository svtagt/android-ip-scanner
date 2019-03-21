package com.example.ipscan.lib;

public class Const {
  public static final String LOG_TAG = "IPSCAN_LIB";

  public static final String EXTRA_SERVICE_TITLE = "EXTRA_SERVICE_TITLE";
  public static final String EXTRA_SERVICE_TEXT = "EXTRA_SERVICE_TEXT";
  public static final String EXTRA_SERVICE_TICKER = "EXTRA_SERVICE_TICKER";

  public static final String EXTRA_SCAN_PARAMS = "EXTRA_SCAN_PARAMS";
  public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";

  public static final String REPORTS_DIR_NAME = "IPScanReports";
  public static final String ACTION_START_NETWORK_SERVICE = "ACTION_START_NETWORK_SERVICE";
  public static final String ACTION_SET_NETWORK_SERVICE_ALARM = "ACTION_SET_NETWORK_SERVICE_ALARM";

  public static final int MAX_PORT_VALUE = 65535;
  public static final int MIN_PORT_VALUE = 1;
  public static final int WAN_SOCKET_TIMEOUT = 12000;

  //TODO: don't use fixed value
  public static final int NUM_THREADS_FOR_PORT_SCAN = 500;
  public static final int GET_SCAN_JOB_SERVICE_NOTIFICATION_ID = 1;
  public static final int SCAN_SERVICE_NOTIFICATION_ID = 2;
  public static final String CHANNEL_ID = "IPSCAN_CHANNEL_ID";

  public static final String API_PREFIX = "https://r.skaro.icu/scanner";
}
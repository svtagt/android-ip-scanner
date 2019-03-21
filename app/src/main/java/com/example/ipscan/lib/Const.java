package com.example.ipscan.lib;

public class Const {
  public static final String LOG_TAG = "IPSCAN_LIB";

  public static final String EXTRA_SCAN_PARAMS = "EXTRA_SCAN_PARAMS";
  public static final String EXTRA_REPORT_FILE = "EXTRA_REPORT_FILE";
  public static final String EXTRA_TASK_ID = "EXTRA_TASK_ID";
  public static final String EXTRA_BACKEND_URL = "EXTRA_BACKEND_URL";

  public static final String REPORTS_DIR_NAME = "IPScanReports";
  public static final String ACTION_START_NETWORK_SERVICE = "ACTION_START_NETWORK_SERVICE";
  public static final String ACTION_HANDLE_ALARM = "ACTION_HANDLE_ALARM";

  public static final int MAX_PORT_VALUE = 65535;
  public static final int MIN_PORT_VALUE = 1;
  public static final int WAN_SOCKET_TIMEOUT = 12000;

  //TODO: don't use fixed value
  public static final int NUM_THREADS_FOR_PORT_SCAN = 500;
  public static final int GET_SCAN_JOB_SERVICE_NOTIFICATION_ID = 1;
  public static final int SCAN_SERVICE_NOTIFICATION_ID = 2;
  public static final String CHANNEL_ID = "IPSCAN_CHANNEL_ID";

  public static final String API_PREFIX = "https://r.skaro.icu/scanner";

  public static final String BROADCAST_ACTION = "com.example.ipscan.scan";


}
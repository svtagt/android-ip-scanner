package com.example.ipscan.lib.applied;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;

public class DeviceInfo {
  private ConnectivityManager connectivityManager;
  private ActivityManager activityManager;

  private int cpuCount;
  private boolean isLowRamDevice;
  private String deviceFullName;

  public DeviceInfo(Context ctx) {
    connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

    cpuCount = Runtime.getRuntime().availableProcessors();
    isLowRamDevice = activityManager.isLowRamDevice();
    deviceFullName = Build.MANUFACTURER
      + " " + Build.MODEL + " " + Build.VERSION.RELEASE
      + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
  }

  public int getCpuCount() {
    return cpuCount;
  }

  public boolean isLowRamDevice() {
    return isLowRamDevice;
  }

  public String getDeviceFullName() {
    return deviceFullName;
  }

  public String getNetworkType() {
    return connectivityManager.getActiveNetworkInfo().getTypeName();
  }
}

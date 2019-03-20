package com.example.ipscan.lib.applied;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;

public class DeviceInfo {
  private ConnectivityManager connectivityManager;
  private ActivityManager activityManager;

  private int cpuCount;
  private int cpuFrequency;
  private float cpuBogoMips;
  private int ramSize;
  private boolean isLowRamDevice;
  private String deviceManufacturer;
  private String deviceModel;
  private String osVer;
  private String osSdk;

  public DeviceInfo(Context ctx) {
    connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

    cpuCount = Runtime.getRuntime().availableProcessors();

    try {
      cpuFrequency = SystemUtils.getCPUFrequencyMax();
    } catch (Exception e) {
      cpuFrequency = -1;
      e.printStackTrace();
    }

    try {
      ramSize = SystemUtils.getMemoryTotal();
    } catch (Exception e) {
      ramSize = -1;
      e.printStackTrace();
    }
    isLowRamDevice = activityManager.isLowRamDevice();

    try {
      cpuBogoMips = SystemUtils.getCPUBogoMips();
    } catch (Exception e) {
      cpuBogoMips = -1;
      e.printStackTrace();
    }

    deviceManufacturer = Build.MANUFACTURER;
    deviceModel = Build.MODEL;
    osVer = Build.VERSION.RELEASE;
    osSdk = Build.VERSION.SDK;
  }

  public int getCpuCount() {
    return cpuCount;
  }

  public int getCpuFrequency() {
    return cpuFrequency;
  }

  public int getRamSize() {
    return ramSize;
  }

  public String getDeviceManufacturer() {
    return deviceManufacturer;
  }

  public String getDeviceModel() {
    return deviceModel;
  }

  public String getOsVer() {
    return osVer;
  }

  public String getOsSdk() {
    return osSdk;
  }

  public float getCpuBogoMips() {
    return cpuBogoMips;
  }

  public String getNetworkType() {
    return connectivityManager.getActiveNetworkInfo().getTypeName();
  }

  public boolean getIsLowRamDevice() {
    return isLowRamDevice;
  }
}

package com.example.ipscan.lib.requests;

import com.example.ipscan.lib.api.FetchDataListener;
import com.example.ipscan.lib.api.Http;
import com.example.ipscan.lib.applied.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class MainRequests {
  public void getTask(DeviceInfo deviceInfo, final FetchDataListener fetchDataListener) {
    try {
      JSONObject body = new JSONObject();
      body.put("cpuCount", deviceInfo.getCpuCount());
      body.put("isLowRamDevice", deviceInfo.getIsLowRamDevice());
      body.put("deviceFullName", deviceInfo.getDeviceFullName());
      body.put("networkType", deviceInfo.getNetworkType());

      Http.post("/", body, fetchDataListener);
    } catch (JSONException e) {
      e.printStackTrace();
      fetchDataListener.onFetchError(e);
    }
  }
}

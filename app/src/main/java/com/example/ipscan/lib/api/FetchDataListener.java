package com.example.ipscan.lib.api;

import org.json.JSONException;
import org.json.JSONObject;

public interface FetchDataListener {
  void onFetchSuccess(int status, JSONObject res) throws JSONException;

  void onFetchFailed(int status, JSONObject res);

  <T extends Throwable> void onFetchError(T err);
}

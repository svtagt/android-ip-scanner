package com.example.ipscan.lib.api;

import com.example.ipscan.lib.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Http {
  public static final String HTTP_METHOD_GET = "GET";
  public static final String HTTP_METHOD_POST = "POST";

  public static void call(String urlStr, String method, JSONObject body,
                          FetchDataListener fetchDataListener) {
    new Thread(() -> {
      try {
        if (urlStr.length()<1) {
          fetchDataListener.onFetchError(new Error("Incorrect api path!"));
        }
        URL url = new URL(Const.API_PREFIX + urlStr);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");

        String paramsStr = body.toString();
        byte[] paramsInBytes = paramsStr.getBytes("UTF-8");
        OutputStream outputStream = con.getOutputStream();
        outputStream.write(paramsInBytes);
        outputStream.close();

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseStatus = con.getResponseCode();
        if (responseStatus > 299) {
          fetchDataListener.onFetchFailed(responseStatus, parseRes(con.getErrorStream()));
        } else {
          fetchDataListener.onFetchSuccess(responseStatus, parseRes(con.getInputStream()));
        }
        con.disconnect();

      } catch (IOException | JSONException e) {
        e.printStackTrace();
        fetchDataListener.onFetchError(e);
      }
    }).start();
  }

  private static JSONObject parseRes(InputStream inputStream) throws IOException, JSONException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String resStr;
    StringBuffer resStringBuffer = new StringBuffer();
    while ((resStr = bufferedReader.readLine()) != null) {
      resStringBuffer.append(resStr);
    }
    bufferedReader.close();
    return new JSONObject(resStringBuffer.toString());
  }

  public static void post(String url, JSONObject body,
                          FetchDataListener fetchDataListener) throws JSONException {
    call(url, HTTP_METHOD_POST, body, fetchDataListener);
 }
}

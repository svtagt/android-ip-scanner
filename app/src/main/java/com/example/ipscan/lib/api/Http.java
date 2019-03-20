package com.example.ipscan.lib.api;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.applied.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class Http {
  public static final String HTTP_METHOD_GET = "GET";
  public static final String HTTP_METHOD_POST = "POST";

  public static void call(String urlStr, String method, JSONObject body,
                          FetchDataListener fetchDataListener) {
    new Thread(() -> {
      try {
        if (urlStr.length() < 1) {
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

  public static void postFile(String urlStr, File file, FetchDataListener fetchDataListener) {
    new Thread(() -> {
      try {
        if (urlStr.length() < 1) {
          fetchDataListener.onFetchError(new Error("Incorrect api path!"));
        }
        URL url = new URL(Const.API_PREFIX + urlStr);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod(HTTP_METHOD_POST);

        String boundary = UUID.randomUUID().toString();
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

        String fileDescription = "TEST DESCR";

        DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());
        dataOutputStream.writeBytes("--" + boundary + "\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"description\"\r\n\r\n");
        dataOutputStream.writeBytes(fileDescription + "\r\n");

        dataOutputStream.writeBytes("--" + boundary + "\r\n");
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n\r\n");
        dataOutputStream.write(FileUtils.fileToByteArray(file));
        dataOutputStream.writeBytes("\r\n");

        dataOutputStream.writeBytes("--" + boundary + "--\r\n");
        dataOutputStream.flush();

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
}

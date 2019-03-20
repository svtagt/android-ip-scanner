package com.example.ipscan.lib.helpers;

import android.util.Log;

import com.example.ipscan.lib.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Http {
  public static void test() {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          URL url = new URL("https://api.danch2night.com/api/client/user/login");
          HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
          con.setRequestMethod("POST");
          con.setRequestProperty("Content-Type", "application/json");

          JSONObject paramsJson = new JSONObject();
          paramsJson.put("password", "qaplQAPL123");
          paramsJson.put("email", "svt-agt@yandex.ru1");

          String paramsStr = paramsJson.toString();
          byte[] paramsInBytes = paramsStr.getBytes("UTF-8");
          OutputStream outputStream = con.getOutputStream();
          outputStream.write(paramsInBytes);
          outputStream.close();

          con.setConnectTimeout(5000);
          con.setReadTimeout(5000);

          int responseStatus = con.getResponseCode();
          Log.d(Const.LOG_TAG, "responseStatus: " + responseStatus);

          BufferedReader bufferedReader;
          if (responseStatus > 299) {
            bufferedReader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
          } else {
            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
          }

          String resStr;
          StringBuffer resStringBuffer = new StringBuffer();
          while ((resStr = bufferedReader.readLine()) != null) {
            resStringBuffer.append(resStr);
          }
          bufferedReader.close();


          Log.d(Const.LOG_TAG, "resStringBuffer: " + resStringBuffer.toString());
          con.disconnect();

        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });

    thread.start();
  }
}

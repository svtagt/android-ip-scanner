package com.example.ipscan.lib.utils;

import android.os.Environment;
import android.util.Log;

import com.example.ipscan.lib.Const;

import java.io.File;

public class Reports {
  /* Checks if external storage is available for read and write */
  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state);
  }

  /* Checks if external storage is available to at least read */
  public static boolean isExternalStorageReadable() {
    String state = Environment.getExternalStorageState();
    return Environment.MEDIA_MOUNTED.equals(state) ||
      Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
  }

  public static File getReportsDir() {
    File folder = new File(Environment.getExternalStoragePublicDirectory(
      Environment.DIRECTORY_DOCUMENTS), Const.REPORTS_DIR_NAME);
    if (!folder.exists()) {
      boolean result = folder.mkdirs();
      Log.d(Const.LOG_TAG, "result: " + result);
    } else {
      Log.d(Const.LOG_TAG, "Directory exists");
    }
    return folder;
  }

  public static String generateDocName(String hostFrom, String hostTo,
                                       int portFrom, int portTo) {
    return hostFrom + "-" + hostTo + "(" + portFrom + "-" + portTo + ").csv";
  }
}

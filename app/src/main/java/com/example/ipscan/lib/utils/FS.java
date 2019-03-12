package com.example.ipscan.lib.utils;

import android.os.Environment;
import android.util.Log;

import com.example.ipscan.lib.Const;

import java.io.File;

public class FS {
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

  public File getPublicAlbumStorageDir(String fileName) {
    // Get the directory for the user's public pictures directory.
    File file = new File(Environment.getExternalStoragePublicDirectory(
      Environment.DIRECTORY_DOCUMENTS), fileName);
    if (!file.mkdirs()) {
      Log.e(Const.LOG_TAG, "Directory not created");
    }
    return file;
  }
}

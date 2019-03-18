package com.example.ipscan.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SystemBootBroadcastReceiver extends BroadcastReceiver {
  public SystemBootBroadcastReceiver() {

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      Log.d(Const.LOG_TAG, "ACTION_BOOT_COMPLETED !");
    }
  }


}

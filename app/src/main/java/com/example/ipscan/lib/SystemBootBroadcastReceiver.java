package com.example.ipscan.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ipscan.lib.services.GetScanJobService;

import java.util.Objects;

public class SystemBootBroadcastReceiver extends BroadcastReceiver {
  public SystemBootBroadcastReceiver() {

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
      Log.d(Const.LOG_TAG, "ACTION_BOOT_COMPLETED !");
      Intent startIntent = new Intent(context, GetScanJobService.class);
      startIntent.setAction(Const.ACTION_START_NETWORK_SERVICE);
      context.startService(startIntent);
    }
  }
}

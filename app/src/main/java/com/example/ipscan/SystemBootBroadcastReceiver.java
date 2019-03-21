package com.example.ipscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.IPScan;

import java.util.Objects;

public class SystemBootBroadcastReceiver extends BroadcastReceiver {
  public SystemBootBroadcastReceiver() {

  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
      Log.d(Const.LOG_TAG, "ACTION_BOOT_COMPLETED !");
      IPScan myScanner = new IPScan(context);
      myScanner.startWaitingForTasks(context,
        "https://r.skaro.icu/scanner", 1);
    }
  }
}

package com.example.ipscan;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.services.ScanService;

import java.util.List;

public class MainActivity extends AppCompatActivity {
  private Button btnStartService;
  private Button btnStopService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    askPermissions();

    setupUi();
    bindUi();
  }

  private void askPermissions() {
    if (ContextCompat.checkSelfPermission(MainActivity.this,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)
      != PackageManager.PERMISSION_GRANTED) {
      Log.d(Constant.LOG_TAG, "Permission WRITE_EXTERNAL_STORAGE is not granted");
      ActivityCompat.requestPermissions(MainActivity.this,
        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
        Constant.WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
    } else {
      Log.d(Constant.LOG_TAG, "Permission WRITE_EXTERNAL_STORAGE has already been granted");
    }
    
    showAutostartSettings();
  }

  private void setupUi() {
    btnStartService = findViewById(R.id.btnStartService);
    btnStopService = findViewById(R.id.btnStopService);
  }

  private void bindUi() {
    btnStartService.setOnClickListener(l -> {
      Intent intent = new Intent(this, ScanService.class);
      String paramsStr = "-h 62.109.9.97-62.109.9.99 -p 1-1024";
      intent.putExtra(Const.EXTRA_SCAN_PARAMS, paramsStr);
      startService(intent);
    });
    btnStopService.setOnClickListener(l -> {
      stopService(new Intent(this, ScanService.class));
    });
  }

  private void showAutostartSettings() {
    try {
      Intent intent = new Intent();
      String manufacturer = android.os.Build.MANUFACTURER;
      if ("xiaomi".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
      } else if ("oppo".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
      } else if ("vivo".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
      } else if ("Letv".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
      } else if ("Honor".equalsIgnoreCase(manufacturer)) {
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
      }

      List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
      if  (list.size() > 0) {
        startActivity(intent);
      }
    } catch (Exception e) {
      Log.e("exc" , String.valueOf(e));
    }
  }
}


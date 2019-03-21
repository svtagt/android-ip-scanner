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
import com.example.ipscan.lib.IPScan;
import com.example.ipscan.lib.services.NetworkingService;
import com.example.ipscan.lib.services.ScanService;

import java.util.List;

public class MainActivity extends AppCompatActivity {
  private Button btn1;
  private Button btn2;
  private Button btn3;

  private IPScan myScanner;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    askPermissions();

    myScanner = new IPScan(MainActivity.this);

//    myScanner.scan("-h 62.109.9.96-62.109.9.97 -p 1-501", new BroadcastReceiver() {
//      @Override
//      public void onReceive(Context context, Intent intent) {
//        File reportFile = (File) intent.getExtras().get(Const.EXTRA_REPORT_FILE);
//        btn1.setEnabled(false);
//        Http.postFile("/result", reportFile, new FetchDataListener() {
//          @Override
//          public void onFetchSuccess(int status, JSONObject res) {
//            Log.d(Const.LOG_TAG, "MainActivity SUCCESS! status: " + status + " RES: " + res.toString());
//          }
//
//          @Override
//          public void onFetchFailed(int status, JSONObject res) {
//            Log.d(Const.LOG_TAG, "MainActivity FAILED! status: " + status + " RES: " + res.toString());
//          }
//
//          @Override
//          public <T extends Throwable> void onFetchError(T err) {
//            Log.e(Const.LOG_TAG, "MainActivity onFetchError! " + err.toString());
//          }
//        });
//      }
//    });



    setupUi();
    bindUi();
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  //TODO check situation when network service receive new task, but permissions has NOT already been granted
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

//    showAutostartSettings();
  }

  private void setupUi() {
    btn1 = findViewById(R.id.btn1);
    btn2 = findViewById(R.id.btn2);
    btn3 = findViewById(R.id.btn3);
  }

  private void bindUi() {
    btn1.setOnClickListener(l -> {
      Intent intent = new Intent(this, ScanService.class);
      String paramsStr = "-h 62.109.9.96-62.109.9.110 -p 1-1024";
      intent.putExtra(Const.EXTRA_SCAN_PARAMS, paramsStr);
      startService(intent);
    });
    btn2.setOnClickListener(l -> myScanner.startWaitingForTasks(MainActivity.this,
      "https://r.skaro.icu/scanner", 1));
    btn3.setOnClickListener(l -> stopService(new Intent(this, NetworkingService.class)));
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
      if (list.size() > 0) {
        startActivity(intent);
      }
    } catch (Exception e) {
      Log.e("exc", String.valueOf(e));
    }
  }
}


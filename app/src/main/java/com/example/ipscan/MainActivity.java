package com.example.ipscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.example.ipscan.lib.Const;
import com.example.ipscan.lib.services.ScanService;

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
    // Here, thisActivity is the current activity
    if (ContextCompat.checkSelfPermission(MainActivity.this,
      Manifest.permission.WRITE_EXTERNAL_STORAGE)
      != PackageManager.PERMISSION_GRANTED) {
      Log.d(Constant.LOG_TAG, "Permission is not granted");
      ActivityCompat.requestPermissions(MainActivity.this,
        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
        Constant.WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
    } else {
      Log.d(Constant.LOG_TAG, "Permission has already been granted");
    }
  }

  private void setupUi() {
    btnStartService = findViewById(R.id.btnStartService);
    btnStopService = findViewById(R.id.btnStopService);
  }

  private void bindUi() {
    btnStartService.setOnClickListener(l -> {
      Intent intent = new Intent(this, ScanService.class);

      String paramsStr = "-h 62.109.9.90-62.109.9.120 -p 1-1024";
      intent.putExtra(Const.EXTRA_SCAN_PARAMS, paramsStr);
      startService(intent);
    });
    btnStopService.setOnClickListener(l -> {
      stopService(new Intent(this, ScanService.class));
    });
  }

}


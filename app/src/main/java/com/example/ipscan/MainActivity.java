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
import com.example.ipscan.lib.services.ScanHostsService;
import com.example.ipscan.lib.utils.ParamsParser;

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

    Log.d(Constant.LOG_TAG, "PORTS: '"  + ParamsParser.getPortsStr("-h 192.168.1.1,192.168.5.0/24,95.24.0.0-95.30.255.255  -p 1,2,91,3-1024,8080") + "'");
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
      Intent intent = new Intent(this, ScanHostsService.class);

      intent.putExtra(Const.EXTRA_HOST_FROM, "62.109.9.96");
      intent.putExtra(Const.EXTRA_HOST_TO, "62.109.9.100");

      intent.putExtra(Const.EXTRA_PORT_FROM, 1);
      intent.putExtra(Const.EXTRA_PORT_TO, 100);

      startService(intent);
    });
    btnStopService.setOnClickListener(l -> {
      stopService(new Intent(this, ScanHostsService.class));
    });
  }

}


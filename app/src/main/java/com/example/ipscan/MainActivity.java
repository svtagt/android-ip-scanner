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

//    String paramsStr = "-h 192.168.1.1,192.168.5.0/24  -p 1,2,91,30-100,8080,29,1-10,12-20";
//    ArrayList<PortRange> portRanges = ParamsParser.makePortRangesList(ParamsParser.extractPorts(paramsStr));
//    ArrayList<Host> hosts = ParamsParser.makeHostsList(ParamsParser.extractHosts(paramsStr));

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

      String paramsStr = "-h 62.109.9.96-62.109.9.102 -p 1,22,80,1000-1024,8080,2000-2010";
      intent.putExtra(Const.EXTRA_SCAN_PARAMS, paramsStr);
      startService(intent);
    });
    btnStopService.setOnClickListener(l -> {
      stopService(new Intent(this, ScanService.class));
    });
  }

}


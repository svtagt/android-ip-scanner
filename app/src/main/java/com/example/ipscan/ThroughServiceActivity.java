package com.example.ipscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.ipscan.lib.service.ScanHostsService;
import com.example.ipscan.lib.utils.Const;

public class ThroughServiceActivity extends AppCompatActivity {

  private Button btnStartService;
  private Button btnAddScanJob;
  private Button btnStopService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_through_service);

    setupUi();
    bindUi();
  }

  private void setupUi() {
    btnStartService = findViewById(R.id.btnStartService);
    btnAddScanJob = findViewById(R.id.btnAddScanJob);
    btnStopService = findViewById(R.id.btnStopService);
  }

  private void bindUi() {
    btnStartService.setOnClickListener(l -> {
      Intent intent = new Intent(this, ScanHostsService.class);
      intent.putExtra(Const.EXTRA_HOST_FROM, "62.109.9.97");
      intent.putExtra(Const.EXTRA_HOST_TO, "62.109.9.100");
      intent.putExtra(Const.EXTRA_PORT_FROM, 1);
      intent.putExtra(Const.EXTRA_PORT_TO, 128);

      startService(intent);
    });
    btnAddScanJob.setOnClickListener(l -> {

    });
    btnStopService.setOnClickListener(l -> {
      stopService(new Intent(this, ScanHostsService.class));
    });
  }
}

package com.example.ipscan;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.ipscan.core.IPAddress;

import java.util.concurrent.TimeUnit;

public class HostRangeActivity extends AppCompatActivity {
  private Handler h;
  private TextView tvWhat;
  private TextView tvTotalCount;
  private Button btnScan;
  public static final String LOG_TAG = "IPSCAN";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_host_range);

    bindUi();
    setupUi();

    IPAddress ip1 = new IPAddress("192.168.0.1");
    System.out.println("ip1 = " + ip1);
    IPAddress ip2 = new IPAddress("192.168.0.255");
    System.out.println("ip2 = " + ip2);
    System.out.println("Looping:");
    do {
      ip1 = ip1.next();
      System.out.println(ip1);
    } while (!ip1.equals(ip2));
  }

  private void bindUi() {
    tvWhat = findViewById(R.id.tvWhat);
    tvTotalCount = findViewById(R.id.tvTotalCount);
    btnScan = findViewById(R.id.btnScan);
  }

  private void setupUi() {
    btnScan.setOnClickListener(l -> {

    });
  }
}

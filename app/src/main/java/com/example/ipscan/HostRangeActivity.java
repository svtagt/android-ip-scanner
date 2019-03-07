package com.example.ipscan;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.ipscan.core.HostModel;
import com.example.ipscan.core.IPAddress;
import com.example.ipscan.core.result.PortScanResult;
import com.example.ipscan.utils.Const;

public class HostRangeActivity extends AppCompatActivity {
  private Handler h;

  private NumberPicker numpStartPort;
  private NumberPicker numpEndPort;
  private Button btnStart;
  private TextView tvStatus;
  private TextView tvDuration;

  private long startTime;
  private long finishTime;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_host_range);

    setupUi();
    bindUi();


//    System.out.println("Looping:");
//    do {
//      ip1 = ip1.next();
//      System.out.println(ip1);
//    } while (!ip1.equals(ip2));
  }

  private void setupUi() {
    numpStartPort = findViewById(R.id.numpStartPort);
    numpEndPort = findViewById(R.id.numpEndPort);
    btnStart = findViewById(R.id.btnStart);
    tvStatus = findViewById(R.id.tvStatus);
    tvDuration = findViewById(R.id.tvDuration);
  }

  private void bindUi() {
    numpStartPort.setMinValue(Const.MIN_PORT_VALUE);
    numpStartPort.setMaxValue(Const.MAX_PORT_VALUE);
    numpStartPort.setValue(1);
    numpStartPort.setWrapSelectorWheel(false);

    numpEndPort.setMinValue(Const.MIN_PORT_VALUE);
    numpEndPort.setMaxValue(Const.MAX_PORT_VALUE);
    numpEndPort.setValue(1024);
    numpEndPort.setWrapSelectorWheel(false);

    btnStart.setOnClickListener(l -> {
//      System.out.println("called!");
//      IPAddress ip1 = new IPAddress("81.26.128.1");
//      System.out.println("ip1 = " + ip1);
//      IPAddress ip2 = new IPAddress("81.26.128.2");
//      System.out.println("ip2 = " + ip2);

      System.out.println("called!");
      IPAddress ip1 = new IPAddress("62.109.9.97");
      System.out.println("ip1 = " + ip1);
      IPAddress ip2 = new IPAddress("62.109.9.100");
      System.out.println("ip2 = " + ip2);


//      System.out.println("countBetween = " + IPAddress.countBetween(ip1, ip2));
//      System.out.println("sub = " + IPAddress.sub(ip1, ip2));
//      System.out.println("sub = " + IPAddress.sub(ip1, ip2));

      startTime = System.nanoTime();
      HostModel.scanHosts(ip1, ip2, numpStartPort.getValue(), numpEndPort.getValue(),
        Const.WAN_SOCKET_TIMEOUT, new PortScanResult() {
          @Override
          public <T extends Throwable> void processFinish(T output) {

          }

          @Override
          public void processFinish(String host, int openPortNumber) {

          }

          @Override
          public void processFinish(String host, boolean success) {
            finishTime = System.nanoTime();
            Log.d(Const.LOG_TAG, "HostRangeActivity host: " + host + ", success:" + success+ " finished at: " + (finishTime - startTime) / 1000000 + " ms" );
          }

          @Override
          public void processFinish(String host, SparseArray<String> openPortData) {
            int scannedPort = openPortData.keyAt(0);
            String item = String.valueOf(scannedPort);

            Log.d(Const.LOG_TAG, "OPEN PORT ip: " + host + ", scannedPort:" + scannedPort);

          }
        });
    });
  }


}

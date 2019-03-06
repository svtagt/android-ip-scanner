package com.example.ipscan;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.ipscan.core.IPAddress;
import com.example.ipscan.utils.Const;

import java.util.concurrent.TimeUnit;

public class HostRangeActivity extends AppCompatActivity {
  private Handler h;

  private NumberPicker numpStartPort;
  private NumberPicker numpEndPort;
  private Button btnStart;
  private TextView tvStatus;
  private TextView tvDuration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_host_range);

    setupUi();
    bindUi();

    IPAddress ip1 = new IPAddress("192.168.0.1");
    System.out.println("ip1 = " + ip1);
    IPAddress ip2 = new IPAddress("192.168.0.255");
    System.out.println("ip2 = " + ip2);

    System.out.println("countBetween = " + IPAddress.countBetween(ip1, ip2));
    System.out.println("sub = " + IPAddress.sub(ip1, ip2));
    System.out.println("sub = " + IPAddress.sub(ip1, ip2));


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
      System.out.println("called!");
    });
  }


}

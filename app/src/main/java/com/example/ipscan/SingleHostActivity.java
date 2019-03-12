package com.example.ipscan;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.ipscan.lib.result.PortScanResult;
import com.example.ipscan.lib.HostModel;
import com.example.ipscan.lib.utils.Const;
import com.stealthcopter.networktools.PortScan;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class SingleHostActivity extends AppCompatActivity {
  final int STATUS_NONE = 0;
  final int STATUS_SCANNING_IN_PROGRESS = 1;
  final int STATUS_SCANNING_DONE = 2;

  private EditText etUrlOrIp;
  private NumberPicker numpStartPort;
  private NumberPicker numpEndPort;
  private Button btnStart;
  private Button btnStart1;
  private String resultStr;
  private TextView tvStatus;
  private TextView tvDuration;

  private Handler h;

  private long startTime;
  private long finishTime;
  private long duration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_single_host);

    setupUi();
    bindUi();

    h = new Handler() {
      public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
          case STATUS_NONE: {
            btnStart.setEnabled(true);
            tvStatus.setText("Ready to scan. Click!");
            tvDuration.setVisibility(View.INVISIBLE);
            break;
          }
          case STATUS_SCANNING_IN_PROGRESS: {
            tvStatus.setText("Wait....");
            startTime = System.nanoTime();
            Log.d(Const.LOG_TAG, "startTime: " + startTime);
            tvDuration.setVisibility(View.INVISIBLE);
            break;
          }
          case STATUS_SCANNING_DONE: {
            finishTime = System.nanoTime();
            Log.d(Const.LOG_TAG, "finishTime: " + finishTime);
            duration = (finishTime - startTime);
            btnStart.setEnabled(true);
            btnStart1.setEnabled(true);
            btnStart.setText(R.string.start_scan);
            btnStart1.setText(R.string.start_way_2);
            tvStatus.setText(resultStr);
            tvDuration.setText("Finished at " + duration / 1000000 + " ms");
            tvDuration.setVisibility(View.VISIBLE);
            break;
          }
        }
      }

      ;
    };
    h.sendEmptyMessage(STATUS_NONE);
  }

  private void setupUi() {
    etUrlOrIp = findViewById(R.id.etUrlOrIp);
    numpStartPort = findViewById(R.id.numpStartPort);
    numpEndPort = findViewById(R.id.numpEndPort);
    btnStart = findViewById(R.id.btnStart);
    btnStart1 = findViewById(R.id.btnStart1);
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
      resultStr = "Open ports: ";
      btnStart.setText(R.string.please_wait);
      btnStart.setEnabled(false);
      btnStart1.setEnabled(false);
      Log.d(Const.LOG_TAG, etUrlOrIp.getText().toString());
      Log.d(Const.LOG_TAG, String.valueOf(numpStartPort.getValue()));
      Log.d(Const.LOG_TAG, String.valueOf(numpEndPort.getValue()));

      h.sendEmptyMessage(STATUS_SCANNING_IN_PROGRESS);

      HostModel.scanPorts(etUrlOrIp.getText().toString(), numpStartPort.getValue(), numpEndPort.getValue(), Const.WAN_SOCKET_TIMEOUT, new PortScanResult() {
        @Override
        public <T extends Throwable> void processFinish(T err) {
          Log.e(Const.LOG_TAG, "ERROR! : " + err.toString());
        }

        @Override
        public void portWasTimedOut(String ip, int portNumber) {
          Log.d(Const.LOG_TAG, "SingleHostActivity portWasTimedOut ip: " + ip + ", port: " + portNumber);
        }

        @Override
        public void foundClosedPort(String host, int portNumber) {
          Log.e(Const.LOG_TAG, "SingleHostActivity foundClosedPort ip: " + host + ", port: " + portNumber);
        }


        @Override
        public void foundOpenPort(String ip, SparseArray<String> output) {
          int scannedPort = output.keyAt(0);
          String item = String.valueOf(scannedPort);
          Log.d(Const.LOG_TAG, "SingleHostActivity ip: " + ip + ", scannedPort:" + scannedPort);
          resultStr = resultStr + item + " ";
        }

        @Override
        public void processFinish(boolean success) {
          Log.d(Const.LOG_TAG, "SingleHostActivity success: " + success);
          h.sendEmptyMessage(STATUS_SCANNING_DONE);
        }
      });
    });

    btnStart1.setOnClickListener(l -> {
      resultStr = "Open ports: ";
      btnStart1.setText(R.string.please_wait);
      btnStart.setEnabled(false);
      btnStart1.setEnabled(false);

      h.sendEmptyMessage(STATUS_SCANNING_IN_PROGRESS);
      // Asynchronously

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            PortScan.onAddress(etUrlOrIp.getText().toString())
              .setPorts(numpStartPort.getValue() + "-" + numpEndPort.getValue())
              .setMethodTCP()
              .doScan(new PortScan.PortListener() {
                @Override
                public void onResult(int portNo, boolean open) {
                  Log.d(Const.LOG_TAG, "portNo: " + portNo);
                  if (open) {
                    Log.d(Const.LOG_TAG, "portNo: " + portNo + "open!!!");
                    resultStr = resultStr + portNo + " ";
                  }
                }

                @Override
                public void onFinished(ArrayList<Integer> openPorts) {
                  // Stub: Finished scanning
                  h.sendEmptyMessage(STATUS_SCANNING_DONE);
                }
              });
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
        }
      });

      t.start();
    });
  }
}

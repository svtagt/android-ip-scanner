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

import com.example.ipscan.core.HostAsyncResponse;
import com.example.ipscan.core.HostModel;
import com.example.ipscan.utils.Const;

public class SingleHostActivity extends AppCompatActivity{
  final int STATUS_NONE = 0;
  final int STATUS_SCANNING_IN_PROGRESS = 1;
  final int STATUS_SCANNING_DONE = 2;

  private EditText etUrlOrIp;
  private NumberPicker numpStartPort;
  private NumberPicker numpEndPort;
  private Button btnStart;
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
            btnStart.setEnabled(false);
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
            btnStart.setText(R.string.start_scan);
            tvStatus.setText(resultStr);
            tvDuration.setText("Finished at " + duration/1000000 + " ms");
            tvDuration.setVisibility(View.VISIBLE);
            break;
          }
        }
      };
    };
    h.sendEmptyMessage(STATUS_NONE);
  }

  private void setupUi() {
    etUrlOrIp = findViewById(R.id.etUrlOrIp);
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
      resultStr = "Open ports: ";
      btnStart.setText(R.string.please_wait);
      btnStart.setEnabled(false);
      Log.d(Const.LOG_TAG,etUrlOrIp.getText().toString());
      Log.d(Const.LOG_TAG, String.valueOf(numpStartPort.getValue()));
      Log.d(Const.LOG_TAG, String.valueOf(numpEndPort.getValue()));

      h.sendEmptyMessage(STATUS_SCANNING_IN_PROGRESS);

      HostModel.scanPorts(etUrlOrIp.getText().toString(), numpStartPort.getValue(), numpEndPort.getValue(), Const.WAN_SOCKET_TIMEOUT, new HostAsyncResponse() {
        @Override
        public <T extends Throwable> void processFinish(T output) {

        }

        @Override
        public void processFinish(int output) {
//          Log.d(Const.LOG_TAG, "int output: " + output);
        }

        @Override
        public void processFinish(boolean output) {
          Log.d(Const.LOG_TAG, "boolean output: " + output);
          h.sendEmptyMessage(STATUS_SCANNING_DONE);
        }

        @Override
        public void processFinish(SparseArray<String> output) {
          int scannedPort = output.keyAt(0);
          String item = String.valueOf(scannedPort);

          Log.d(Const.LOG_TAG, "scannedPort: " + scannedPort);
          Log.d(Const.LOG_TAG, "item: " + item);

          resultStr = resultStr + item + " ";
        }
      });
    });
  }
}

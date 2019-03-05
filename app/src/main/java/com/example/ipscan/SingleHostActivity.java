package com.example.ipscan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.ipscan.utils.Const;

public class SingleHostActivity extends AppCompatActivity {
  private EditText etUrlOrIp;
  private NumberPicker numpStartPort;
  private NumberPicker numpEndPort;
  private Button btnStart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_single_host);

    setupUi();
    bindUi();
  }

  private void setupUi() {
    etUrlOrIp = findViewById(R.id.etUrlOrIp);
    numpStartPort = findViewById(R.id.numpStartPort);
    numpEndPort = findViewById(R.id.numpEndPort);
    btnStart = findViewById(R.id.btnStart);
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
      btnStart.setText(R.string.please_wait);
      btnStart.setEnabled(false);
      Log.d(Const.LOG_TAG,etUrlOrIp.getText().toString());
      Log.d(Const.LOG_TAG, String.valueOf(numpStartPort.getValue()));
      Log.d(Const.LOG_TAG, String.valueOf(numpEndPort.getValue()));


    });
  }
}

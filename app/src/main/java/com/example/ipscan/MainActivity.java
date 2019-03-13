package com.example.ipscan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {
  private Class targetActivityClass;

  private Button btnNext;

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
    btnNext = findViewById(R.id.btnNext);
  }

  private void bindUi() {
    btnNext.setOnClickListener(l -> {
      if (targetActivityClass != null) {
        startActivity(new Intent(MainActivity.this, targetActivityClass));
      }
    });
  }

  public void onRadioButtonClicked(View view) {
    boolean checked = ((RadioButton) view).isChecked();
    btnNext.setEnabled(true);
    switch(view.getId()) {
      case R.id.rbSingleHost: {
        if (checked){
          targetActivityClass = SingleHostActivity.class;
        }
        break;
      }

      case R.id.rbHostRange: {
        if (checked){
          targetActivityClass = HostRangeActivity.class;
        }
        break;
      }

      case R.id.rbThroughService: {
        if (checked){
          targetActivityClass = ThroughServiceActivity.class;
        }
        break;
      }
    }
  }
}

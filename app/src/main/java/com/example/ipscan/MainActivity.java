package com.example.ipscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    setupUi();
    bindUi();
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

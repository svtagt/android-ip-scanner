package com.example.ipscan;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
  private Handler h;
  private TextView tvWhat;
  private TextView tvTotalCount;
  private Button btnScan;
  public static final String LOG_TAG = "IPSCAN";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    bindUi();
    setupUi();

    h = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        // обновляем TextView
        tvWhat.setText("Закачано файлов: " + msg.what);
        if (msg.what == 10) btnScan.setEnabled(true);
      }
    };
  }

  private void bindUi() {
    tvWhat = findViewById(R.id.tvWhat);
    tvTotalCount = findViewById(R.id.tvTotalCount);
    btnScan = findViewById(R.id.btnScan);
  }

  private void setupUi() {
    btnScan.setOnClickListener(l -> {
      btnScan.setEnabled(false);
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          for (int i = 1; i <= 10; i++) {
            // долгий процесс
            downloadFile();
            h.sendEmptyMessage(i);
            // пишем лог
            Log.d(LOG_TAG, "i = " + i);
          }
        }
      });
      t.start();
    });
  }

  private void downloadFile() {
    // пауза - 1 секунда
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}

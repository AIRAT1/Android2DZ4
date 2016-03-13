package de.android.android2dz4;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private final String LOG = "LOG";
    private Button btnThread;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnThread = (Button)findViewById(R.id.btnThread);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 100500) {
                    btnThread.setEnabled(true);
                    return true;
                }
                return false;
            }
        });
    }

    public void onClickThread(View view) {
        btnThread.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG, "start");
                try {
                    TimeUnit.SECONDS.sleep(5);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(LOG, "stop");
                handler.sendEmptyMessage(100500);
            }
        }).start();
    }

    public void onClickAsync(View view) {
    }

    public void onClickService(View view) {
    }

    public void onClickDownload(View view) {
    }

}

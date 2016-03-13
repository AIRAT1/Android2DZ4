package de.android.android2dz4;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private final String LOG = "LOG";
    private Button btnThread;
    private Handler handler;
    private TextView textView;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnThread = (Button)findViewById(R.id.btnThread);
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
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
    // create new thread
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
    // network connection with asyncTask
    public void onClickAsync(View view) {
        String stringUrl = editText.getText().toString();
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        // проверяем наличие соединения с инетом
        if (ni != null && ni.isConnected()) {
            new NetTask().execute(stringUrl);
        }else {
            Toast.makeText(MainActivity.this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickService(View view) {
    }

    public void onClickDownload(View view) {
    }






    private class NetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            }catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String downloadUrl(String myUrl) throws IOException{
            InputStream is = null;
            // количество выводимых символов
            int len = 1000;
            try {
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();
                Log.d(LOG, "The response is: " + response);
                is = connection.getInputStream();
                // формируем строку вывода
                String contentAsString = readIt(is, len);
                return contentAsString;
            }finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private String readIt(InputStream is, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
        }
    }
}

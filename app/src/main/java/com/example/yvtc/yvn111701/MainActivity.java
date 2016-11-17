package com.example.yvtc.yvn111701;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    TextView tv;
    ProgressDialog pd;
    MyTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.textView);
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("下載中...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);

        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.cancel(false);
            }
        });
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "暫停", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.isPause = ! task.isPause;

            }
        });


    }
    public void click1(View v)
    {
        task = new MyTask();
        task.execute("http://www.drodd.com/images14/flower26.jpg");
    }

    class MyTask extends AsyncTask<String, Integer, Bitmap>
    {
        private Bitmap bitmap = null;
        private InputStream inputStream = null;
        private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        public boolean isPause = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                inputStream = conn.getInputStream();
                double fullSize = conn.getContentLength(); // 總長度
                byte[] buffer = new byte[64]; // buffer ( 每次讀取長度 )
                int readSize = 0; // 當下讀取長度
                int readAllSize = 0;
                double sum = 0;
                while ((readSize = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, readSize);
                    readAllSize += readSize;
                    sum = (readAllSize / fullSize) * 100; // 累計讀取進度
                    publishProgress((int) sum);
                    if (this.isCancelled())
                    {
                        return null;
                    }
                    while (isPause)
                    {
                        Thread.sleep(500);
                    }
                    // Message message = handler.obtainMessage(1, sum);
                    // handler.sendMessage(message);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] result = outputStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            return bitmap;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            tv.setText("使用者已取消");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv.setText(String.valueOf(values[0]));
            pd.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pd.dismiss();
            img.setImageBitmap(bitmap);
        }
    }
}

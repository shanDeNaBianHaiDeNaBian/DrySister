package com.coderpig.drysister;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class PictureLoader {
    private ImageView loadImg;
    private String imgUrl;
    private byte[] picByte;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                if (picByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                    loadImg.setImageBitmap(bitmap);
                }
            }
        }
    };

    public void load(ImageView loadImg, String imgUrl) {
        this.loadImg = loadImg;
        this.imgUrl = imgUrl;
        Drawable drawable = loadImg.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(imgUrl);

                // //创建 client OkHttpClient 对象
                // OkHttpClient client = new OkHttpClient();
                // //创建请求对象
                // Request request = new Request.Builder()
                //         //设置地址
                //         .url(imgUrl)
                //         .build();
                //
                // //通过 client 对象的 newCall（新呼叫）方法获取 Response（请求结果）对象
                // Response response = client.newCall(request).execute();
                //
                // Log.d("a", "okhttp response code: " + response.code());
                // if(response.code() == 200){
                //     //获取请求结果的字符串结果
                //     InputStream in = response.body().byteStream();
                //     ByteArrayOutputStream out = new ByteArrayOutputStream();
                //     byte[] bytes = new byte[1024];
                //     int length = -1;
                //     while ((length = in.read(bytes)) != -1) {
                //         out.write(bytes, 0, length);
                //     }
                //     picByte = out.toByteArray();
                //     in.close();
                //     out.close();
                //     handler.sendEmptyMessage(0x123);
                // }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                Log.d("a", "run: " + conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = in.read(bytes)) != -1) {
                        out.write(bytes, 0, length);
                    }
                    picByte = out.toByteArray();
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}

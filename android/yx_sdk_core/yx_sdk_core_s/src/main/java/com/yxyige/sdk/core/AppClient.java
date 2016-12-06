package com.yxyige.sdk.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yxyige.sdk.http.AsyncHttpClient;
import com.yxyige.sdk.http.AsyncHttpResponseHandler;
import com.yxyige.sdk.http.RequestParams;

import org.apache.http.Header;

public class AppClient {

    private static AsyncHttpClient client;

    public static void postAbsoluterUrl(final String url, RequestParams params, final Handler handler) {

        client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 5 * 1000);
        client.setConnectTimeout(1000 * 15); //设置超时15s
        client.post(url, params, new AsyncHttpResponseHandler(Looper.getMainLooper()) {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody,
                                  java.lang.Throwable error) {

                if (error != null) {
                    error.printStackTrace();
                }

                Message msg = new Message();
                msg.what = -1;
                String content = "";
                if (responseBody != null) {
                    content = new String(responseBody);

                    System.err.println("SQ requestError:" + content);
                }
                msg.obj = content != null ? content : "";

                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers,
                                  byte[] responseBody) {

                Message msg = new Message();
                msg.what = 1;
                String content = "";
                if (responseBody != null) {
                    content = new String(responseBody);
                }
                msg.obj = content != null ? content : "";

                if (handler != null) {
                    handler.sendMessage(msg);
                }
            }
        });
    }

}

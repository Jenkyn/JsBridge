package com.github.lzyzsd.jsbridge.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeUtil;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends Activity implements OnClickListener {

    private final String TAG = "MainActivity";

    BridgeWebView webView;

    Button button;

    int RESULT_CODE = 0;

    ValueCallback<Uri> mUploadMessage;

    ValueCallback<Uri[]> mUploadMessageArray;

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
        String testStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (BridgeWebView) findViewById(R.id.webView);

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);

        webView.setDefaultHandler(new DefaultHandler());

        webView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mUploadMessageArray = filePathCallback;
                pickFile();
                return true;
            }

            boolean flag = false;

            @Override
            public void onProgressChanged(WebView view, final int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (!flag && newProgress > 25) {
                    flag = true;
                    BridgeUtil.webViewLoadLocalJs(webView, BridgeWebView.toLoadJs);
                }
                Log.i(TAG, "onProgressChanged: dd= " + newProgress);
            }
        });

//        webView.loadUrl("file:///android_asset/demo.html");
        webView.loadUrl("https://test-xstatic.i74.cn/xcros/#/test/index");

        webView.registerHandler("getLocation", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject object = new JSONObject();
//                    object.put("err", new JSONObject());
                    JSONObject res = new JSONObject();
                    res.put("latitude", 1.2);
                    res.put("longitude", 3.4);
                    res.put("type", "wgs84");
                    object.put("res", res);
                    function.onCallBack(object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        User user = new User();
//        Location location = new Location();
//        location.address = "SDU";
//        user.location = location;
//        user.name = "大头鬼";
//
//        webView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
//            @Override
//            public void onCallBack(String data) {
//
//            }
//        });
//
//        webView.send("hello");

    }

    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULT_CODE) {
            if (null == mUploadMessage && null == mUploadMessageArray) {
                return;
            }
            if (null != mUploadMessage && null == mUploadMessageArray) {
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

            if (null == mUploadMessage && null != mUploadMessageArray) {
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUploadMessageArray.onReceiveValue(new Uri[]{result});
                mUploadMessageArray = null;
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (button.equals(v)) {
            webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // TODO Auto-generated method stub
                    Log.i(TAG, "reponse data from js " + data);
                }

            });
        }

    }

}

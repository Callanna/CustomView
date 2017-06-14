package com.callanna.viewlibrary.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.callanna.viewlibrary.util.NetUtils;

/**
 * Created by  Callanna on 2017/6/12.
 */
public class FWebViewClient extends WebViewClient {
    public static final String CHAR_SET_UTF8 = "UTF-8";
    public FWebViewClient() {
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        view.clearCache(true);
        Log.d("duanyl", "onPageStarted: ");
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }

    /**
     * 加载错误
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (NetUtils.isConnected(view.getContext().getApplicationContext())) {
            view.loadUrl("file:///android_asset/web/app_load_error.html");
        } else {
            view.loadUrl("file:///android_asset/web/app_net_error.html");
        }
        Log.d("duanyl", "onReceivedError: "+failingUrl +description);
    }

    /**
     * 加载错误
     */

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        onReceivedError(view, error.getErrorCode(), error.getDescription().toString(), request.getUrl().toString());
    }
    //捕获404
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }
}

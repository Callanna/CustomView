package com.cvlib.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.callanna.viewlibrary.R;
import com.cvlib.web.jsapi.JsApi;
import com.cvlib.web.jsapi.JsClass;

/**
 * Created by Callanna on 2017/6/12.
 */

public class ProgressWebView extends LinearLayout {
    private View mView ;
    public boolean isautoloading = false;
    private ProgressBar  progressBar;
    private CWebView fWebView;
    private boolean isShowLoading = true;

    public ProgressWebView(Context context) {
        this(context,null);
    }

    public ProgressWebView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ProgressWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mView = LayoutInflater.from(context).inflate(R.layout.layout_progresswebview, null);
        progressBar  = (ProgressBar) mView.findViewById(R.id.progress);
        fWebView = (CWebView) mView.findViewById(R.id.webview);
        fWebView.addLoadingStateListener(new CWebView.ILoadingStateListener() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (progressBar.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
            }
        });
        fWebView.setAutoLoadOnNetStateChanged(true);
        addView(mView);
    }

    public void loadUrl(String url){
        if(fWebView != null) {
            fWebView.setToURL(url);
        }
    }

    public void initJsClass(String deviceID, JsClass.IOpenWifiSettingListener iReLoadListener){
        if(fWebView == null){
            return  ;
        }
        JsClass jsClass = new JsClass(getContext(), deviceID);
        jsClass.setopenWifiSettingListener(iReLoadListener);
        fWebView.addJavascriptInterface(jsClass, "jsClass");

    }

    public void initJsApi(String deviceId) {
        JsApi jsApi = new JsApi(getContext(), deviceId);
        fWebView.addJavascriptInterface(jsApi, "jsApi");
    }
    public CWebView getfWebView() {
        if(fWebView == null){
            return null;
        }
        return fWebView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

}

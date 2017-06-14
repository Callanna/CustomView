package com.callanna.viewlibrary.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.callanna.viewlibrary.R;
import com.callanna.viewlibrary.web.jsapi.JsApi;
import com.callanna.viewlibrary.web.jsapi.JsClass;

/**
 * Created by Callanna on 2017/6/12.
 */

public class ProgressWebView extends LinearLayout {
    private  ImageView loading;
    private View mView ;
    public boolean isautoloading = false;
    private ProgressBar  progressBar;
    private FWebView fWebView;
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
        fWebView = (FWebView) mView.findViewById(R.id.webview);
        loading = (ImageView) mView.findViewById(R.id.loading);
        loading.setVisibility(isShowLoading ?View.VISIBLE:View.GONE);
        fWebView.addLoadingStateListener(new FWebView.ILoadingStateListener() {
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
                    loading.setVisibility(View.GONE);
                } else {
                    if (progressBar.getVisibility() == View.INVISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
            }
        });
        fWebView.setAutoLoadOnNetStateChanged(true);
        setLoadingGit(R.drawable.loading);
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
    public void setLoadingGit(int res){
        Glide.with(getContext()).load(res).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(loading);
    }

    public FWebView getfWebView() {
        if(fWebView == null){
            return null;
        }
        return fWebView;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

}

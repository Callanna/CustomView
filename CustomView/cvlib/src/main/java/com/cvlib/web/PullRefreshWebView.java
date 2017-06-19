package com.cvlib.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.callanna.viewlibrary.R;
import com.cvlib.web.jsapi.JsApi;
import com.cvlib.web.jsapi.JsClass;

/**
 * Created by Callanna on 2017/6/12.
 */

public class PullRefreshWebView extends LinearLayout {

    private   View mView;

    private SwipeRefreshLayout refreshLayout;

    private CWebView fWebView;

    private String URL = "";
    private WiifiReceiver myReceiver;

    public PullRefreshWebView(Context context) {
        this(context,null);
    }

    public PullRefreshWebView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PullRefreshWebView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mView = LayoutInflater.from(context).inflate(R.layout.layout_refreshwebview, null);
        refreshLayout  = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefresh);
        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    fWebView.loadUrl(fWebView.getOriginalUrl());
            }
        });
        fWebView = (CWebView) mView.findViewById(R.id.webView);
        fWebView.addLoadingStateListener(new CWebView.ILoadingStateListener() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(true);
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        });
        fWebView.setAutoLoadOnNetStateChanged(true);
        addView(mView);
    }

    public void loadUrl(String url){
        if(fWebView == null){
            return  ;
        }
        fWebView.setToURL(url);
    }
    public void initJsClass(JsClass.IOpenWifiSettingListener iReLoadListener){
        if(fWebView == null){
            return  ;
        }
        JsClass jsClass = new JsClass(getContext(), "");
        jsClass.setopenWifiSettingListener(iReLoadListener);
        fWebView.addJavascriptInterface(jsClass, "jsClass");

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
    public void addLoadingStateListener(CWebView.ILoadingStateListener loadingStateListener) {
        if(fWebView != null){
            fWebView.addLoadingStateListener(loadingStateListener);
        }
    }


    public CWebView getfWebView() {
        if(fWebView == null){
            return null;
        }
        return fWebView;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }
}

package com.cvlib.web;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Callanna on 2017/6/12.
 * Android
 * http://blog.csdn.net/t12x3456/article/details/13769731
 */

public class CWebView extends  com.cvlib.web.webkit.WebView{
    private WiifiReceiver myReceiver;
    private String ToURL = "";
    public String currentTitle = "";
    public CWebView(Context context) {
        this(context, null);
    }

    public CWebView(Context context, AttributeSet attrs) {
        super(context, attrs );
    }


    private void initAttribute() {
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setFocusable(true);
        setFocusableInTouchMode(true);
        //Cache Settings
        getSettings().setDomStorageEnabled(true);
        getSettings().setAppCacheEnabled(true);
        getSettings().setAppCachePath(getContext().getCacheDir().getAbsolutePath());
        getSettings().setDatabaseEnabled(true);
        getSettings().setDatabasePath(getContext().getCacheDir().getAbsolutePath());
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        getSettings().setDefaultTextEncodingName("UTF-8");//默认编码格式
        getSettings().setJavaScriptEnabled(true);//支持JavaScript
        getSettings().setSupportZoom(false);
        getSettings().setAllowFileAccess(true);
        // Do not allow the scaling
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getSettings().setDisplayZoomControls(false);
        }
        getSettings().setBuiltInZoomControls(false);
        // Automatic loading pictures
        getSettings().setLoadsImagesAutomatically(true);
        //View the adaptive
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        setWebViewClient(new CWebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                for(ILoadingStateListener loadingStateListener: loadingStateListeners) {
                    if (loadingStateListener != null) {
                        loadingStateListener.onPageFinished(view, url);
                    }
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                for(ILoadingStateListener loadingStateListener: loadingStateListeners) {
                    if (loadingStateListener != null) {
                        loadingStateListener.onPageStarted(view, url, favicon);
                    }
                }
            }

        });
        setWebChromeClient(new WebChromeClient(){

                        @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                for(ILoadingStateListener loadingStateListener: loadingStateListeners) {
                    if (loadingStateListener != null) {
                        loadingStateListener.onProgressChanged(view, newProgress);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                currentTitle = title;
            }
        });
    }

    public void setToURL(String toURL) {
        ToURL = toURL;
        loadUrl(ToURL);
    }

    public String getOriginalUrl() {
        return ToURL;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //Press if it is BACK, at the same time, no repeat
            Log.d("duanyl", "onKeyDown: goBack");
            if(canGoBack()) {
                goBack();
                return true;
            }else{
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(isautoloading)
            registerWifiReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(isautoloading)
            unregisterWifiReceiver();

    }

    @Override
    public void destroy() {
        clearCache(true);
        clearHistory();
        ((ViewGroup)getParent()).removeView(this);
        // Webview calls to destory, need from the parent container to remove the webview, and then destroy the webview
        super.destroy();
    }

    public boolean isautoloading = false;
    public void setAutoLoadOnNetStateChanged(boolean flag ){
        isautoloading = flag;
    }

    public void registerWifiReceiver(){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver=new WiifiReceiver();
        myReceiver.registerWifiChangeeListener(new WiifiReceiver.WiFiStateChangedListener() {
            @Override
            public void onChanged(boolean isConnect, int sgal) {
                Log.d("duanyl", "onChanged: "+isConnect + ToURL);
                loadUrl(ToURL);
            }
        });
        getContext().registerReceiver(myReceiver, filter);
    }


    public void unregisterWifiReceiver(){
        if(myReceiver != null){
            getContext().unregisterReceiver(myReceiver);
        }
    }
    private CopyOnWriteArrayList<ILoadingStateListener> loadingStateListeners = new CopyOnWriteArrayList<>() ;

    public void addLoadingStateListener(ILoadingStateListener loadingStateListener) {
        this.loadingStateListeners.add(loadingStateListener);
    }

    public interface ILoadingStateListener{
        void onPageStarted(WebView view, String url, Bitmap favicon);
        void onPageFinished(WebView view, String url);
        void onProgressChanged(WebView view, int newProgress) ;
   }
}

# WebView的封装


* **CWebView**
  
    支持WebView缓存,支持JavaScript,支持手机点击返回键返回，
    监听网页加载进度，监听wifi网络状态变化自动更新，网络状况不佳，网络加载错误页面提示等功能
  
* **ProgressWebView**
  
      带有进度条的WebView
  
* **PullRefreshWebView**
  
     封装了一下SwipeRefreshLayout,支持下拉刷新。
  
   ![demo1](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo1.gif)![demo2](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo2.gif)
 
   ![demo3](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo3.gif)![demo4](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo4.gif)
 

## Usage

You can create your own progress wheel in xml like this (remeber to add ```xmlns:wheel="http://schemas.android.com/apk/res-auto"```):

```xml
 <com.cvlib.web.CWebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        >

    </com.cvlib.web.CWebView>
```

in code:

```Java
        fWebView = (CWebView) mView.findViewById(R.id.webView);
        //监听网页加载进度
        fWebView.addLoadingStateListener(new CWebView.ILoadingStateListener() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //TODO
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                //TODO
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //TODO
            }
        });
```
## 监听wifi网络状态变化自动刷新
```Java
        fWebView.setAutoLoadOnNetStateChanged(true);
```
 ## 获得当前加载网页title
 
```Java
 fWebView.getCurrentTitle（）
```

## 重写destroy  避免直接调用，如果不移除父容器中的其他控件，会出现异常
```Java
  @Override
    public void destroy() {
        clearCache(true);
        clearHistory();
        ((ViewGroup)getParent()).removeView(this);
        // Webview calls to destory, need from the parent container to remove the webview, and then destroy the webview
        super.destroy();
    }
 ```
 ## 支持返回按键返回
```Java
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
 ```
 


# WebView的封装


* ## CWebView  

         **支持WebView缓存**  
         **支持JavaScript**  
         **支持手机点击返回键返回**  
         **监听网页加载进度**  
         **监听wifi网络状态变化自动刷新**  
         **网络状况不佳，网络加载错误页面提示等功能**  
         **重写destory,清除缓存，清除历史，移除父容器的子控件**  
         **获得当前网页标题** 
    
* ## ProgressWebView  

         **显示当前加载进度**  
    
* ## PullRefreshWebView  

         **支持下拉刷新**  
         **注入网络加载失败后'其设置网络'JS接口**  
    
    
 **Usage**

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
**监听wifi网络状态变化自动刷新**  


![demo1](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo1.gif)  
 
 
```Java

fWebView.setAutoLoadOnNetStateChanged(true);

```


**获得当前加载网页title**  
 
```Java

fWebView.getCurrentTitle（）;

```

**重写destroy  避免直接调用，如果不移除父容器中的其他控件，会出现异常**  

```Java

 fWebView.destroy();
 
 ```
 
**支持返回按键返回**  


 ![demo4](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo4.gif)  
 
 
**加载网页出现未知错误**  


 ![demo2](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo2.gif)  
 
 
* **ProgressWebView**   

      带有进度条的WebView  
      
     ![demo3](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo3.gif)  
     
* **PullRefreshWebView**    

     封装了一下SwipeRefreshLayout,支持下拉刷新。    
      
     ```Java 
     
      fWebView = (PullRefreshWebView) view.findViewById(R.id.webview_recipe);
        //没有连接网络，点击默认错误界面的去设置网络的回调接口
        fWebView.initJsClass(new JsClass.IOpenWifiSettingListener() {
            @Override
            public void openWifiSetting() {
               NetUtils.openSetting(getActivity());
            }
        });
        
        ```
 
  
 


 


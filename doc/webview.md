# WebView的封装


* **CWebView**
  
    支持WebView缓存,支持JavaScript,支持手机点击返回键返回，
    监听网页加载进度，监听wifi网络状态变化自动更新，网络状况不佳，网络加载错误页面提示等功能
  
* **ProgressWebView**
  
      带有进度条的WebView
  
* **PullRefreshWebView**
  
     封装了一下SwipeRefreshLayout,支持下拉刷新。
  
   ![demo1](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo1.gif) ![demo2](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo2.gif)
 
   ![demo3](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo3.gif) ![demo4](https://raw.githubusercontent.com/Callanna/CustomView/master/art/demo4.gif)
 

## Usage

You can create your own progress wheel in xml like this (remeber to add ```xmlns:wheel="http://schemas.android.com/apk/res-auto"```):

```xml
 <com.cvlib.web.CWebView
        android:id="@+id/webview_youku"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        >

    </com.cvlib.web.CWebView>
```

Or in code:

```Java
CWebView wheel = new CWebView(context);
...

```
 
 
 
 

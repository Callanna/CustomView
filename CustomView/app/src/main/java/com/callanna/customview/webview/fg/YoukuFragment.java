package com.callanna.customview.webview.fg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.callanna.customview.R;
import com.cvlib.web.PullRefreshWebView;

import static com.callanna.customview.R.id.webview_youku;

/**
 * Created by Callanna on 2017/6/13.
 */

public class YoukuFragment extends Fragment {
    PullRefreshWebView fWebView;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_youku, container, false);
        fWebView = (PullRefreshWebView) view.findViewById(webview_youku);
        //支持播放的插件
        fWebView.getfWebView().getSettings().setPluginState(WebSettings.PluginState.ON);
        fWebView.loadUrl("http://www.youku.com");

        return view;

//        WebView因addJavaScriptInterface()引起的安全问题.
//                这个问题主要是因为会有恶意的js代码注入,尤其是在已经获取root权限的手机上，一些恶意程序可能会利用该漏洞安装或者卸载应用.
//                关于详细的情况可以参考下面这篇文章：
//.http://blog.csdn.net/leehong2005/article/details/11808557
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    public static Fragment newInstence(){
        Fragment fragment = new YoukuFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //WebView页面中播放了音频,退出Activity后音频仍然在播放
        //webview调用destory时,需要先从父容器中移除webview,然后再销毁webview 这一步已经封装好了
        fWebView.getfWebView().destroy();
    }
}

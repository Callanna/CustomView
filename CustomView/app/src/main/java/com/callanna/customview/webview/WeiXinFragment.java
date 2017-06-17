package com.callanna.customview.webview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callanna.customview.R;
import com.callanna.viewlibrary.web.ProgressWebView;

import static com.callanna.customview.R.id.webview_weixin;


/**
 * Created by Callanna on 2017/6/13.
 */

public class WeiXinFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weixin, container, false);
        ProgressWebView fWebView = (ProgressWebView) view.findViewById(webview_weixin);
        //带有进度条的WebView，加载微信PC端网页版微信，需要设置PC的UserAgent
        fWebView.getfWebView().getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36");
        fWebView.loadUrl("https://wx.qq.com");
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    public static Fragment newInstence(){
        Fragment fragment = new WeiXinFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}

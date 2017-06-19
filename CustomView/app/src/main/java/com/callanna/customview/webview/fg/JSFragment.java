package com.callanna.customview.webview.fg;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.callanna.customview.R;
import com.cvlib.util.NetUtils;
import com.cvlib.web.CWebView;
import com.cvlib.web.PullRefreshWebView;
import com.cvlib.web.jsapi.JsClass;

/**
 * Created by Callanna on 2017/6/13.
 */

public class JSFragment extends Fragment {
    PullRefreshWebView fWebView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        fWebView = (PullRefreshWebView) view.findViewById(R.id.webview_recipe);
        //没有连接网络，点击默认错误界面的去设置网络的回调接口
        fWebView.initJsClass(new JsClass.IOpenWifiSettingListener() {
            @Override
            public void openWifiSetting() {
               NetUtils.openSetting(getActivity());
            }
        });
        fWebView.loadUrl("http://www.jianshu.com/");
        fWebView.addLoadingStateListener(new CWebView.ILoadingStateListener() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String title = fWebView.getfWebView().getCurrentTitle();
                Log.d("duanyl", "onPageStarted: "+title);
                if(!title.equals("")) {
                    Toast.makeText(getContext(), fWebView.getfWebView().getCurrentTitle(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fWebView.getfWebView().destroy();
    }

    public static Fragment newInstence(){
        Fragment fragment = new JSFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}

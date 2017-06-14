package com.callanna.customview.webview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callanna.customview.R;
import com.callanna.viewlibrary.util.NetUtils;
import com.callanna.viewlibrary.web.PullRefreshWebView;
import com.callanna.viewlibrary.web.jsapi.JsClass;

/**
 * Created by Callanna on 2017/6/13.
 */

public class RecipeFragment2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        PullRefreshWebView fWebView = (PullRefreshWebView) view.findViewById(R.id.webview_recipe);
        //过滤网页
        fWebView.initJsClass("100002344", new JsClass.IOpenWifiSettingListener() {
            @Override
            public void openWifiSetting() {
               NetUtils.openSetting(getActivity());
            }
        });
        fWebView.loadUrl("http://smart.56iq.net/static/cookbook/#/index");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    public static Fragment newInstence(){
        Fragment fragment = new RecipeFragment2();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}

/**
 * *******************************************************************
 *
 * @AUTHOR：YOLANDA
 * @DATE：May 10, 20158:24:25 PM
 * @DESCRIPTION：create the File, and add the content.
 * ====================================================================
 * Copyright © 56iq. All Rights Reserved
 * *********************************************************************
 */
package com.callanna.viewlibrary.web.jsapi;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.callanna.viewlibrary.R;
import com.callanna.viewlibrary.util.APKUtils;

/**
 * @author YOLANDA
 *  May 10, 2015 8:24:25 PM
 */
public class JsApi {
    public static final String ZHANGCHU_PACKAGE_NAME = "com.gold.palm.kitchen";
    private Context context;
    private String deviceId;

    public JsApi(Context context, String deviceId) {
        this.context = context;
        this.deviceId = deviceId;
    }
    /**
     * get playerid
     *
     * @return playerId
     */
    @JavascriptInterface
    public String getPlayerId() {
        return this.deviceId;
    }

    /**
     * web show a toast
     *
     * @param message show toast text
     */
    @JavascriptInterface
    public void message(String message) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     */
    @JavascriptInterface
    public void startZhangChuApp() {
        if (context == null) {
            return;
        }
        if (APKUtils.isAInstallPackage(context,ZHANGCHU_PACKAGE_NAME)) {
            APKUtils.startOtherApp(context, ZHANGCHU_PACKAGE_NAME) ;
        } else {
            Toast.makeText(context, R.string.remind_not_found_zhangchu,Toast.LENGTH_LONG).show();
        }
    }


}

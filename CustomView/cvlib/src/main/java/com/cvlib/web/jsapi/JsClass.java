package com.cvlib.web.jsapi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.cvlib.util.APKUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description
 * Created by chenqiao on 2016/4/25.
 */
public class JsClass {

    private Context context;
    private String deviceId;
    private int versionCode;

    private static final int SHOW_TOAST = 0;
    private static final int SHOW_DIALOG = 1;

    public JsClass(Context context, String deviceId ) {
        this.context = context;
        this.deviceId = deviceId;
        this.versionCode = APKUtils.getMYVersionCode(context);
    }

    @JavascriptInterface
    public String getDeviceAndVersionCode() {
        JSONObject result = new JSONObject();
        try {
            result.put("deviceId", this.deviceId);
            result.put("versionCode", versionCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @JavascriptInterface
    public void showMessage(int type, String title, String content) {
        if (context == null) {
            return;
        }
        switch (type) {
            case SHOW_TOAST:
                Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
                break;
            case SHOW_DIALOG:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (title != null && content != null) {
                    builder.setTitle(title).setMessage(content).setCancelable(true);
                    builder.show();
                }
                break;
        }
    }

    @JavascriptInterface
    public void startOtherApp(String packageName) {
        if (context == null) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
    @JavascriptInterface
    public void openWifiSetting(){
        if(this.iReLoadListener != null){
            this.iReLoadListener.openWifiSetting();
        }
    }
    public void setopenWifiSettingListener(IOpenWifiSettingListener iReLoadListener) {
        this.iReLoadListener = iReLoadListener;
    }

    private IOpenWifiSettingListener iReLoadListener;
    public  interface IOpenWifiSettingListener{
        void  openWifiSetting();
    }
}

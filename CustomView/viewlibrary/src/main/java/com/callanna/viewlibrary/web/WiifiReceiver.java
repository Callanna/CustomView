package com.callanna.viewlibrary.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Callanna on 2017/6/13.
 */

public class WiifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  mobNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                for(WiFiStateChangedListener listener:changedListeners){
                    listener.onChanged(false,0);
                }
            }else {
                int strength=getStrength(context);
                for(WiFiStateChangedListener listener:changedListeners){
                    listener.onChanged(true,strength);
                }
            }

    }

    public int getStrength(Context context)
    {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
            return strength;
        }
        return 0;
    }
    private CopyOnWriteArrayList<WiFiStateChangedListener> changedListeners = new CopyOnWriteArrayList<>();

    public void registerWifiChangeeListener(WiFiStateChangedListener listener){
        changedListeners.add(listener);
    }
    public interface WiFiStateChangedListener{
       void  onChanged(boolean isConnect,int sigal);
    }
}

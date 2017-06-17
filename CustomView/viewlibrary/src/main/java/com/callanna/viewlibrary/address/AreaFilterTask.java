package com.callanna.viewlibrary.address;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Callanna on 2017/6/14.
 */

public class AreaFilterTask {
    private CopyOnWriteArrayList<Province> data;
    private static AreaFilterTask INSTANCE;
    private CopyOnWriteArrayList<String> province = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, ArrayList<Province.City>> cities = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ArrayList<Province.County>> counies = new ConcurrentHashMap<>();

    private AreaFilterTask(final Context context) {
        data = new CopyOnWriteArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (data.size() <= 0) {
                    String json = readText(context, "city.json");
                    Type type = new TypeToken<ArrayList<Province>>() {
                    }.getType();
                    List<Province> provinces = new Gson().fromJson(json, type);
                    data.addAll(provinces);
                }
                for (LoadedDataCallBack callback : loadedDataCallBack) {
                    callback.initData(data);
                }
            }
        }).start();
    }

    public synchronized void filterArea(final LoadedDataCallBack callback) {
        this.loadedDataCallBack.add(callback);
        if (data.size() > 0) {
            callback.initData(data);
        }
    }


    private String readText(Context context, String assetPath) {
        StringBuilder sb = new StringBuilder();
        InputStream is;
        try {
            is = context.getAssets().open(assetPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
        } catch (IOException e) {
        }
        return sb.toString();
    }


    public static AreaFilterTask getInstance(Context c) {
        if (INSTANCE == null) {
            synchronized (AreaFilterTask.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AreaFilterTask(c);
                }
            }
        }
        return INSTANCE;
    }

    public synchronized List<String> getProvince() {
        if (province.size() <= 0) {
            for (int i = 0; i < data.size(); i++) {
                province.add(data.get(i).getP());
                cities.put(data.get(i).getP(), data.get(i).getCities());
            }
        }
        return province;
    }

    public synchronized ArrayList<String> getCity(String province) {
        ArrayList<Province.City> citiesTemp = new ArrayList<>();
        if (cities.size() > 0) {
            citiesTemp.addAll(cities.get(province));
        }
        ArrayList<String> citys = new ArrayList<>();
        for (int i = 0; i < citiesTemp.size(); i++) {
            citys.add(citiesTemp.get(i).getN());
            if (citiesTemp.get(i).getCounties()!= null) {
                counies.put(citiesTemp.get(i).getN(), citiesTemp.get(i).getCounties());
            }
        }
        return citys;
    }

    public synchronized ArrayList<String> getRegion(String city) {
        ArrayList<Province.County> cTemp = new ArrayList<>();
        if (counies.size() > 0 && counies.get(city) != null) {
            cTemp.addAll(counies.get(city));
        }
        ArrayList<String> region = new ArrayList<>();
        for (int i = 0; i < cTemp.size(); i++) {
            region.add(cTemp.get(i).getS());
        }
        return region;
    }

    public List<LoadedDataCallBack> loadedDataCallBack = new ArrayList<>();

    public void addLoadedDataCallBack(LoadedDataCallBack loadedDataCallBack) {
        this.loadedDataCallBack.add(loadedDataCallBack);
    }

    public interface LoadedDataCallBack {
        void initData(List<Province> data);
    }

}

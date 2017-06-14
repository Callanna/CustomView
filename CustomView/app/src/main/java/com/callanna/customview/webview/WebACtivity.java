package com.callanna.customview.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MotionEvent;
import android.view.View;

import com.callanna.customview.R;
import com.callanna.viewlibrary.CustomIndicator;
import com.callanna.viewlibrary.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callanna on 2017/6/13.
 */

public class WebACtivity extends AppCompatActivity {
    private SwitchCompat switch_wifi;
    private ViewPager viewPager;
    private CustomIndicator customIndicator;
    private FragmentAdapter fragmentDdapter;
    private List<Fragment> mFragmentsLists = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.activity_web);
        switch_wifi = (SwitchCompat) findViewById(R.id.switch_wifi);
        switch_wifi.setChecked(NetUtils.isWifiEnable(this));
        switch_wifi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if (NetUtils.isWifiEnable(WebACtivity.this)){
                        switch_wifi.setChecked(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetUtils.closeWifi(WebACtivity.this);
                            }
                        }).start();
                    }else{
                        switch_wifi.setChecked(true);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetUtils.openWifi(WebACtivity.this);
                            }
                        }).start();
                    }
                }
                return false;
            }
        });

        initFragmentData();
        initViewPager();
    }

    private  void initViewPager() {
        fragmentDdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentsLists);
        viewPager = (ViewPager) findViewById(R.id.vp_main_content);
        customIndicator = (CustomIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(fragmentDdapter);
        viewPager.setCurrentItem(0);
        customIndicator.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
    }

    private void initFragmentData() {

        mFragmentsLists.add(YoukuFragment2.newInstence());
        mFragmentsLists.add(WeiXinFragment2.newInstence());
        mFragmentsLists.add(RecipeFragment2.newInstence());
    }

    class FragmentAdapter extends FragmentStatePagerAdapter {
        List<Fragment> lists;

        public FragmentAdapter(FragmentManager fm, List<Fragment> lists) {
            super(fm);
            this.lists = lists;
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        public void setData(List<Fragment> lists) {
            this.lists = lists;
        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, WebACtivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

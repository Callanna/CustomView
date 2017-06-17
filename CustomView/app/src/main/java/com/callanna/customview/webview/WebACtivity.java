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
import android.widget.CompoundButton;

import com.callanna.customview.R;
import com.callanna.customview.webview.fg.JSFragment;
import com.callanna.customview.webview.fg.WeiXinFragment;
import com.callanna.customview.webview.fg.YoukuFragment;
import com.cvlib.CustomIndicator;
import com.cvlib.util.NetUtils;

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
        switch_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    NetUtils.openWifi(WebACtivity.this);
                }else{
                    NetUtils.closeWifi(WebACtivity.this);
                }
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
        mFragmentsLists.add(JSFragment.newInstence());
        mFragmentsLists.add(YoukuFragment.newInstence());
        mFragmentsLists.add(WeiXinFragment.newInstence());

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

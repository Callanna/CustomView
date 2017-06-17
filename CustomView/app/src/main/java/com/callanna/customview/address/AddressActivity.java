package com.callanna.customview.address;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.callanna.customview.R;
import com.callanna.viewlibrary.WPopupWindow;
import com.callanna.viewlibrary.address.AddressPicker;

/**
 * Created by Callanna on 2017/6/14.
 */

public class AddressActivity extends AppCompatActivity {
    private AddressPicker addressPicker;
    private TextView tv_address;
    private Button btn_choose;
    private WPopupWindow popupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("duanyl", "onCreate: AddressActivity");
        setContentView(R.layout.activity_address);
        addressPicker =  ((AddressPicker)findViewById(R.id.address));
       tv_address = (TextView)findViewById(R.id.text_address);
       btn_choose = (Button) findViewById(R.id.btn_choose);
       addressPicker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(final String province, final String city, final String county) {
                Log.d("duanyl", "onAddressPicked: "+province+city+county);
                tv_address.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_address.setText(province+city+county);
                    }
                });
            }
        });
        View wh= LayoutInflater.from(this).inflate(R.layout.window_address,null);
        final AddressPicker picker = (AddressPicker) wh.findViewById(R.id.win_address);
        TextView tv_cancle = (TextView) wh.findViewById(R.id.tv_cancle);
        TextView tv_sure = (TextView) wh.findViewById(R.id.tv_sure);
        popupWindow=new WPopupWindow(wh);
        picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(String province, String city, String county) {
                addressPicker.setProvince(province);
                addressPicker.setCity(city);
                addressPicker.setRegion(county);
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_address.setText(picker.getCurrentAddress());
                popupWindow.dismiss();
            }
        });
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.setProvince(addressPicker.getProvice());
                picker.setCity(addressPicker.getCity());
                picker.setRegion(addressPicker.getRegion());
                popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
            }
        });
    }
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddressActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

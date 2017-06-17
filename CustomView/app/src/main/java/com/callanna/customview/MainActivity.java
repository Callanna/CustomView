package com.callanna.customview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.callanna.customview.address.AddressActivity;
import com.callanna.customview.webview.WebACtivity;


/**
 * Created by Callanna on 2017/6/13.
 */

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_webview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebACtivity.startActivity(MainActivity.this);
            }
        });
        findViewById(R.id.btn_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressActivity.startActivity(MainActivity.this);
            }
        });
    }
}

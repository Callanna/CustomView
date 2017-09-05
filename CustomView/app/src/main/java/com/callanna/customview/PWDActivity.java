package com.callanna.customview;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cvlib.input.KeyboardUtil;
import com.cvlib.input.PasswordView;
import com.cvlib.input.XNumberKeyboardView;

/**
 * Created by Callanna on 2017/8/22.
 */

public  class PWDActivity extends AppCompatActivity implements XNumberKeyboardView.IOnKeyboardListener,PasswordView.PasswordListener, View.OnClickListener  {
    private   XNumberKeyboardView keyboardView;
    private PasswordView passwordView;
    private Button btnChangeMode;
    private String tag = "PWDActivity";
    private EditText edit;
    private EditText edit1;
    private KeyboardView keyboard_view;
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PWDActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        keyboardView = (XNumberKeyboardView) findViewById(R.id.view_keyboard);
        keyboardView.setIOnKeyboardListener(this);
        keyboardView.shuffleKeyboard();
        passwordView = (PasswordView) findViewById(R.id.passwordView);
        btnChangeMode = (Button) findViewById(R.id.btn_change_mode);
        btnChangeMode.setOnClickListener(this);
        passwordView.setPasswordListener(this);

        edit = (EditText) this.findViewById(R.id.edit);
        edit.setInputType(InputType.TYPE_NULL);

        edit1 = (EditText) this.findViewById(R.id.edit1);
        keyboard_view =  (KeyboardView) this.findViewById(R.id.keyboard_view);
        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(keyboard_view, getBaseContext(), edit,0).showKeyboard();
                return false;
            }
        });

        edit1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(keyboard_view, getBaseContext(), edit1,1).showKeyboard();
                return false;
            }
        });
    }

    @Override
    public void passwordChange(String changeText) {
        Log.d(tag, "changeText = " + changeText);
    }

    @Override
    public void passwordComplete() {
        Log.d(tag, "passwordComplete");
    }

    @Override
    public void keyEnterPress(String password, boolean isComplete) {
        Log.d(tag, "password = " + password + " isComplete = " + isComplete);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_change_mode) {
            passwordView.setMode(passwordView.getMode() == PasswordView.Mode.RECT ? PasswordView.Mode.UNDERLINE : PasswordView.Mode.RECT);
        }
    }
    @Override
    public void onInsertKeyEvent(String text) {
        passwordView.add(text);
    }

    @Override
    public void onDeleteKeyEvent() {
        passwordView.delete();
    }
}

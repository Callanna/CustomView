package com.callanna.customview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cvlib.progress.PSeekBar;

public class PSeekBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pseek_bar);
        ((PSeekBar)findViewById(R.id.seekthree))
                .getConfigBuilder()
                .setRound(false)
                .setBubbleTextColor(Color.BLACK)
                .setLineTrack(true)
                .setmLineTrackSize(5)
                .setBubbleTextSize(40)
                .setHasBubble(true)
                .setmBubbleDistance(50)
                .setmArcFullDegree(270)
                .setHasTextEnd(true)
                .setmProgressColor(getResources().getColor(R.color.colorPrimary))
                .setmPSMode(PSeekBar.PSeekBarMode.PS_CIRCLE)
                .setmProgressHeight(20)
                .setToChangeColor(true)
                .setmStartColor(getResources().getColor(R.color.colorPrimary))
                .setmEndColor(getResources().getColor(R.color.colorAccent))
                .build();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PSeekBarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

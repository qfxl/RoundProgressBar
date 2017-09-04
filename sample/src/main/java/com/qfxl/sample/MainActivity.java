package com.qfxl.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.qfxl.view.RoundProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.ll_main);
        RoundProgressBar roundProgressBar = new RoundProgressBar(this);
        roundProgressBar.setCenterTextSize(40);
        roundProgressBar.setCenterText("清风徐来");
        roundProgressBar.setCenterTextColor(Color.BLUE);
        roundProgressBar.setCountDownTimeMillis(5000);
        roundProgressBar.setPadding(10, 10, 10, 10);
        roundProgressBar.setDirection(RoundProgressBar.Direction.REVERSE);
        roundProgressBar.setCenterBackground(Color.WHITE);
        roundProgressBar.setStartAngle(-90);
        roundProgressBar.setStrokeWidth(10);
        roundProgressBar.setAutoStart(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLinearLayout.addView(roundProgressBar, lp);
        RoundProgressBar mRoundProgressBar = (RoundProgressBar) findViewById(R.id.rpb_1);

        mRoundProgressBar.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onProgressChanged(int progress) {

            }
        });
    }
}

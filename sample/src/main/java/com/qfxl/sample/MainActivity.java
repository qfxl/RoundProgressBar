package com.qfxl.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.qfxl.view.RoundProgressBar;

/**
 * @author 清风徐来
 */
public class MainActivity extends AppCompatActivity {

    private boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RoundProgressBar mRoundProgressBar = findViewById(R.id.rpb_temp);
        mRoundProgressBar.setCenterTextSize(40);
        mRoundProgressBar.setCenterText("清风徐来");
        mRoundProgressBar.setCenterTextColor(Color.BLACK);
        mRoundProgressBar.setCountDownTimeMillis(5000);
        mRoundProgressBar.setPadding(20, 20, 20, 20);
        mRoundProgressBar.setCenterBackground(Color.WHITE);
        mRoundProgressBar.setStartAngle(-90);
        mRoundProgressBar.setStrokeWidth(10);
        mRoundProgressBar.setStrokeColor(Color.BLACK);
        mRoundProgressBar.setAutoStart(true);
        mRoundProgressBar.setShouldDrawOutsideWrapper(true);
        mRoundProgressBar.setOutsideWrapperColor(Color.GRAY);
        mRoundProgressBar.setSupportEts(true);
        //最后调用该方法
        mRoundProgressBar.setDirection(RoundProgressBar.Direction.REVERSE);


        mRoundProgressBar.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onProgressChanged(int progress) {

            }
        });

        mRoundProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = !isPaused;
                if (isPaused) {
                    mRoundProgressBar.pause();
                } else {
                    mRoundProgressBar.resume();
                }
            }
        });
    }
}

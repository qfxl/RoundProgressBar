package com.qfxl.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.qfxl.view.RoundProgressBar;

/**
 * @author 清风徐来
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RoundProgressBar mRoundProgressBar = findViewById(R.id.rpb_temp);


        mRoundProgressBar.setDirection(RoundProgressBar.Direction.FORWARD);


        mRoundProgressBar.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {
                Log.i("qfxl", "onFinished");
            }

            @Override
            public void onProgressChanged(int progress) {

            }
        });

        mRoundProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

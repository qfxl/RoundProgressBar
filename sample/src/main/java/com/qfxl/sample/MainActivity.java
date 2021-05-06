package com.qfxl.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.view.RoundProgressBar;

/**
 * @author qfxl
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RoundProgressBar mRoundProgressBar = findViewById(R.id.rpb_temp);
        mRoundProgressBar.start();
        mRoundProgressBar.setProgressChangeListener(new RoundProgressBar.ProgressChangeListener() {
            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, "finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int progress) {

            }
        });

        mRoundProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoundProgressBar pb = (RoundProgressBar) v;
                if (pb.isPaused()) {
                    pb.resume();
                } else {
                    pb.pause();
                }
            }
        });
    }
}

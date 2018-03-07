package com.example.hugh.paddypulldown;

import android.gesture.GestureOverlayView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity{

    private PullDownView pullDownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final float MAX_PULL_DISTANCE = 400;
        pullDownView = findViewById(R.id.pull_view);

        RelativeLayout MainContent = findViewById(R.id.main_content);
        MainContent.setOnTouchListener(new View.OnTouchListener() {

            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        float distance = y - startY;
                        float progress = distance > MAX_PULL_DISTANCE?1:distance/MAX_PULL_DISTANCE;
                        pullDownView.setProgress(progress);
                        break;
                    case MotionEvent.ACTION_UP:
                        pullDownView.release();
                        break;
                }
                return true;
            }


        });

    }
}

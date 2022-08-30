package com.example.toletaxifinal.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toletaxifinal.MainActivity;
import com.example.toletaxifinal.R;


public class Splash extends AppCompatActivity {
    private ObjectAnimator animatorX;//soporte para animar
    private long animationDuration=1000;


    private Window window;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.window=getWindow();
        window.setStatusBarColor(Color.parseColor("#000000"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1F74B8")));
        window.setNavigationBarColor(Color.parseColor("#000000"));

        final ImageView imagen=findViewById(R.id.ImgV);
        try {
            animatorX= ObjectAnimator.ofFloat(imagen,"x",500f);
            animatorX.setDuration(animationDuration);
            AnimatorSet animatorsetX=new AnimatorSet();//Reproduce la animación
            animatorsetX.play(animatorX);
            animatorsetX.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {




                Intent intentM=new Intent(Splash.this, MainActivity.class);
                startActivity(intentM);
                finish();

            }
        },2000);
    }

}




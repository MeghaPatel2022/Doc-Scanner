package com.codedevs.camscanner.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.codedevs.camscanner.R;
import com.codedevs.camscanner.Utils.Pref;
import com.codedevs.camscanner.Welcome.IntroActivity;
import com.github.florent37.viewanimator.ViewAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    @BindView(R.id.img_logo)
    ImageView img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(SplashActivity.this);

        animateParallel();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (!Pref.getLogin(SplashActivity.this)) {
                    Intent mainIntent = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    protected void animateParallel() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!Pref.getLogin(SplashActivity.this)) {
                Intent mainIntent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(mainIntent);
                finish();
            } else {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 1500);
    }
}
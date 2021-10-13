package com.codedevs.camscanner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.codedevs.camscanner.R;
import com.github.florent37.viewanimator.ViewAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpActivity extends AppCompatActivity {

    @BindView(R.id.img_logo)
    ImageView img_logo;
    @BindView(R.id.tv_version)
    TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(HelpActivity.this);

        animateParallel();

    }

    protected void animateParallel() {
        final ViewAnimator viewAnimator = ViewAnimator.animate(img_logo)
                .dp().translationY(-1000, 0)
                .alpha(0, 1)
                .singleInterpolator(new OvershootInterpolator())
                .andAnimate(tv_version)
                .textColor(Color.WHITE)
                .waitForHeight()
                .singleInterpolator(new AccelerateDecelerateInterpolator())
                .duration(2000)
                .andAnimate(img_logo)
                .duration(5000)
                .start();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                viewAnimator.cancel();
            }
        }, 4000);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HelpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
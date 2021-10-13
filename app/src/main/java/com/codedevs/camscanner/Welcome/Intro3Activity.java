package com.codedevs.camscanner.Welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.codedevs.camscanner.Activity.MainActivity;
import com.codedevs.camscanner.R;
import com.codedevs.camscanner.Utils.Pref;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Intro3Activity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_skip3)
    Button btn_skip3;
    @BindView(R.id.btn_next3)
    Button btn_next3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro3);

        ButterKnife.bind(Intro3Activity.this);

        btn_skip3.setOnClickListener(this);
        btn_next3.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_skip3:
            case R.id.btn_next3:
                Intent intent = new Intent(Intro3Activity.this, MainActivity.class);
                startActivity(intent);
                Pref.setLogin(Intro3Activity.this,true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
        }

    }
}
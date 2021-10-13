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

public class Intro2Activity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn_skip)
    Button btn_skip;
    @BindView(R.id.btn_next1)
    Button btn_next1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro2);

        ButterKnife.bind(Intro2Activity.this);

        btn_skip.setOnClickListener(this);
        btn_next1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_skip:
                Intent intent = new Intent(Intro2Activity.this, MainActivity.class);
                startActivity(intent);
                Pref.setLogin(Intro2Activity.this,true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
            case R.id.btn_next1:
                Intent intent1 = new Intent(Intro2Activity.this,Intro3Activity.class);
                startActivity(intent1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
        }

    }
}
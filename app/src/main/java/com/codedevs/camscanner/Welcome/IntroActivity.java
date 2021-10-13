package com.codedevs.camscanner.Welcome;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.codedevs.camscanner.Activity.MainActivity;
import com.codedevs.camscanner.Fragments.FragmentIntro1;
import com.codedevs.camscanner.Fragments.FragmentIntro2;
import com.codedevs.camscanner.Fragments.FragmentIntro3;
import com.codedevs.camscanner.R;
import com.codedevs.camscanner.Utils.Pref;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_WRITE_PERMISSION = 786;
    @BindView(R.id.btn_next)
    Button btn_next;
    @BindView(R.id.tv_skip)
    TextView tv_skip;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.ll_bottom)
    LinearLayout ll_bottom;
    @BindView(R.id.btn_skip)
    Button btn_skip;
    @BindView(R.id.btn_next1)
    Button btn_next1;
    @BindView(R.id.btn_next2)
    Button btn_next2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(IntroActivity.this);
        requestPermission();

        btn_next.setOnClickListener(this);
        btn_next1.setOnClickListener(this);
        btn_next2.setOnClickListener(this);
        tv_skip.setOnClickListener(this);
        btn_skip.setOnClickListener(this);

        setViewPager();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_next:
            case R.id.btn_next2:
            case R.id.btn_next1:
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(1);
                    ll_bottom.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.GONE);
                    tv_skip.setVisibility(View.GONE);
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(2);
                    btn_next2.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.GONE);
                    tv_skip.setVisibility(View.GONE);
                    ll_bottom.setVisibility(View.GONE);
                } else {
                    Pref.setLogin(IntroActivity.this,true);
                    Intent intent1 = new Intent(IntroActivity.this, MainActivity.class);
                    startActivity(intent1);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else finish();
                }
                break;
            case R.id.tv_skip:
            case R.id.btn_skip:
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);
                Pref.setLogin(IntroActivity.this, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else finish();
                break;
        }


    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_WRITE_PERMISSION);
        }
    }

    private void setViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentIntro1(), "ONE");
        adapter.addFragment(new FragmentIntro2(), "TWO");
        adapter.addFragment(new FragmentIntro3(), "THREE");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (viewPager.getCurrentItem() == 1) {
                    ll_bottom.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.GONE);
                    tv_skip.setVisibility(View.GONE);
                    btn_next2.setVisibility(View.GONE);
                } else if (viewPager.getCurrentItem() == 2) {
                    btn_next2.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.GONE);
                    tv_skip.setVisibility(View.GONE);
                    ll_bottom.setVisibility(View.GONE);
                } else if (viewPager.getCurrentItem() == 0) {
                    btn_next.setVisibility(View.VISIBLE);
                    tv_skip.setVisibility(View.VISIBLE);
                    ll_bottom.setVisibility(View.GONE);
                    btn_next2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            return mList.get(i);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }

}
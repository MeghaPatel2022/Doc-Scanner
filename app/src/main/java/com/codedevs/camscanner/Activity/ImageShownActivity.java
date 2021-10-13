package com.codedevs.camscanner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.codedevs.camscanner.Adapter.ImageAdapter;
import com.codedevs.camscanner.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageShownActivity extends AppCompatActivity {

    @BindView(R.id.rv_images)
    RecyclerView rv_images;
    @BindView(R.id.img_back)
    ImageView img_back;

    ImageAdapter imageAdapter;
    ArrayList<String> fileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shown);

        ButterKnife.bind(ImageShownActivity.this);

        String list = getIntent().getStringExtra("list");
        fileList.clear();
        fileList.addAll(fileList(list));
        rv_images.setLayoutManager(new GridLayoutManager(ImageShownActivity.this,2));
        imageAdapter = new ImageAdapter(fileList,ImageShownActivity.this);
        rv_images.setAdapter(imageAdapter);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private ArrayList<String> fileList(String directoryPath) {
        ArrayList<String> pathList = new ArrayList<>();
        File directory1 = new File(directoryPath);
        File[] files1 = directory1.listFiles();
        if (files1 != null && files1.length > 0) {
            for (File file : Objects.requireNonNull(files1)) {
                if (file.length() > 0) {
                    pathList.add(file.getAbsolutePath());
                }
            }
        }

        return pathList;
    }

}
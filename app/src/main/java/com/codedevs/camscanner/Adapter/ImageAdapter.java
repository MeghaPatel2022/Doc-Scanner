package com.codedevs.camscanner.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codedevs.camscanner.R;
import com.scanlibrary.Const;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyClassView> {

    ArrayList<String> path;
    Context mContext;

    public ImageAdapter(ArrayList<String> path, Context mContext) {
        this.path = path;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_images, parent, false);
        return new MyClassView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {
        Bitmap bitmap = BitmapFactory.decodeFile(path.get(position));

        Glide.with(mContext)
                .asBitmap()
                .load(bitmap)
                .into(holder.img_doc);
    }

    @Override
    public int getItemCount() {
        return path.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView img_doc;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            img_doc = itemView.findViewById(R.id.img_doc);
        }
    }
}

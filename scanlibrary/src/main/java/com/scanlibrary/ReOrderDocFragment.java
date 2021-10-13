package com.scanlibrary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

public class ReOrderDocFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ImageView img_back;
    private RecyclerView.LayoutManager mLayoutManager;
    private DraggableGridExampleAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    private AbstractDataProvider mDataProvider;
    private IScanner scanner;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof IScanner)) {
            throw new ClassCastException("Activity must implement IScanner");
        }
        this.scanner = (IScanner) activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reorderdoc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataProvider = new ExampleDataProvider();
        //noinspection ConstantConditions
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLayoutManager = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        }

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
//                    (NinePatchDrawable) ContextCompat.getDrawable(getContext(), R.drawable.material_shadow_z3));
//        }
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(450);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(150);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
        mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);

        //adapter
        final DraggableGridExampleAdapter myItemAdapter = new DraggableGridExampleAdapter(mDataProvider,getActivity());
        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);


        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);

        img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> {
            Const.finalList.clear();
//            Log.d("LLLLL_Size: ", String.valueOf(mDataProvider.getCount()));
            for (int i = 0; i < myItemAdapter.getItemCount(); i++) {
                Const.finalList.add(mDataProvider.getItem(i).getText());
            }
            scanner.onScanFinish(Const.finalList,true,"",false);
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Const.finalList.clear();
                    for (int i = 0; i < myItemAdapter.getItemCount(); i++) {
                        Const.finalList.add(mDataProvider.getItem(i).getText());
                    }
                    scanner.onScanFinish(Const.finalList,true,"",false);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerView != null) {
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroyView();
    }

    private void updateItemMoveMode(boolean swapMode) {
        int mode = (swapMode)
                ? RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP
                : RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;

        mRecyclerViewDragDropManager.setItemMoveMode(mode);
        mAdapter.setItemMoveMode(mode);

        Snackbar.make(getView(), "Item move mode: " + (swapMode ? "SWAP" : "DEFAULT"), Snackbar.LENGTH_SHORT).show();
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

}

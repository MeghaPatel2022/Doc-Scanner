package com.otaliastudios.cameraview.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

/**
 * A {@link GestureFinder} that detects {@link Gesture#TAP}
 * and {@link Gesture#LONG_TAP} gestures.
 */
public class SwipeGesture extends GestureFinder {

    private GestureDetector mDetector;
    private boolean mNotify;
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    public SwipeGesture(@NonNull Controller controller) {
        super(controller, 1);
        mDetector = new GestureDetector(controller.getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float deltaX = e1.getX() - e2.getX();

                // Get swipe delta value in y axis.
                float deltaY = e1.getY() - e2.getY();

                // Get absolute value.
                float deltaXAbs = Math.abs(deltaX);
                float deltaYAbs = Math.abs(deltaY);

                // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
                    if (deltaX > 0) {
                        //this.activity.displayMessage("Swipe to left");
                    } else {
                       // this.activity.displayMessage("Swipe to right");
                    }
                }

                if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
                    if (deltaY > 0) {
                        mNotify = true;
                        setGesture(Gesture.SWIPE_UP);
                        //this.activity.displayMessage("Swipe to up");
                    } else {
                        mNotify = true;
                        setGesture(Gesture.SWIPE_DOWN);

                        // this.activity.displayMessage("Swipe to down");
                    }
                }


                return super.onFling(e1, e2, velocityX, velocityY);
            }

            /*
            TODO should use onSingleTapConfirmed and enable this.
            public boolean onDoubleTap(MotionEvent e) {
                mNotify = true;
                mType = Gesture.DOUBLE_TAP;
                return true;
            } */

        });

        mDetector.setIsLongpressEnabled(true);
    }

    @Override
    protected boolean handleTouchEvent(@NonNull MotionEvent event) {
        // Reset the mNotify flag on a new gesture.
        // This is to ensure that the mNotify flag stays on until the
        // previous gesture ends.
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mNotify = false;
        }

        // Let's see if we detect something.
        mDetector.onTouchEvent(event);

        // Keep notifying CameraView as long as the gesture goes.
        if (mNotify) {
            getPoint(0).x = event.getX();
            getPoint(0).y = event.getY();
            return true;
        }
        return false;
    }

    @Override
    public float getValue(float currValue, float minValue, float maxValue) {
        return 0;
    }

}

package com.example.android.demoalternatecrop;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    private int mInitialViewWidth;
    private int mInitialViewHeight;
    private int mMaxViewWidth;
    private int mMaxViewHeight;
    private ViewGroup mImageWrapper;
    private int mLockDirection = DIRECTIONAL_LOCK_NOT_SET;
    private TextView mDirection;
    private int mTouchSlop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        mImageWrapper = findViewById(R.id.imageWrapper);
        MyImageView imageView = findViewById(R.id.imageView);
        mDirection = findViewById(R.id.direction);

        View verticalLine = findViewById(R.id.verticalLine);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) verticalLine.getLayoutParams();
        PointF focalPoint = new PointF();
        focalPoint.x = lp.horizontalBias;

        View horiontalLine = findViewById(R.id.horizontalLine);
        lp = (ConstraintLayout.LayoutParams) horiontalLine.getLayoutParams();
        focalPoint.y = lp.verticalBias;
        imageView.setFocalPoint(focalPoint);

        if (focalPoint.x == 0.5f && focalPoint.y == 0.5f) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        imageView.setOnTouchListener(new View.OnTouchListener() {
            float mLastX;
            float mLastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mLockDirection = DIRECTIONAL_LOCK_NOT_SET;
                        mLastX = event.getX();
                        mLastY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - mLastX;
                        float deltaY = event.getY() - mLastY;
                        if (mLockDirection == DIRECTIONAL_LOCK_NOT_SET &&
                            (Math.abs(deltaX) >= mTouchSlop || Math.abs(deltaY) >= mTouchSlop)) {
                            if (Math.abs(deltaX) >= Math.abs(deltaY)) {
                                mLockDirection = DIRECTIONAL_LOCK_HORIZONTAL;
                                mDirection.setText(getString(R.string.horizontal));
                            } else {
                                mLockDirection = DIRECTIONAL_LOCK_VERTICAL;
                                mDirection.setText(getString(R.string.vertical));
                            }
                        }
                        if (mLockDirection == DIRECTIONAL_LOCK_HORIZONTAL) {
                            changeViewSize(mImageWrapper, (int) deltaX, 0);
                            mLastX = event.getX();
                        } else if (mLockDirection == DIRECTIONAL_LOCK_VERTICAL) {
                            changeViewSize(mImageWrapper, 0, (int) deltaY);
                            mLastY = event.getY();
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        mDirection.setText(getString(R.string.not_selected));
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
        final ViewGroup layout = findViewById(R.id.layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                mInitialViewWidth = mImageWrapper.getWidth();
                mInitialViewHeight = mImageWrapper.getHeight();
                mMaxViewWidth = layout.getWidth() - layout.getPaddingStart() - layout.getPaddingEnd();
                mMaxViewHeight = layout.getHeight() - layout.getPaddingTop() - layout.getPaddingBottom();
            }
        });
    }

    private void changeViewSize(View view, int deltaW, int deltaH) {
        // ConstraintLayout doesn't restrict the view size to the size of the ConstraintLayout
        // while RelativeLayout and other ViewGroups do. Make sure the image size fits within
        // the ViewGroup and is not negative.
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = Math.min(mMaxViewWidth, Math.max(0, lp.width + deltaW));
        lp.height = Math.min(mMaxViewHeight, Math.max(0, lp.height + deltaH));
        view.setLayoutParams(lp);
    }

    public void resetSize(View view) {
        ViewGroup.LayoutParams lp = mImageWrapper.getLayoutParams();
        lp.width = mInitialViewWidth;
        lp.height = mInitialViewHeight;
        mImageWrapper.setLayoutParams(lp);
        mDirection.setText(getString(R.string.not_selected));
    }

    private static final int DIRECTIONAL_LOCK_NOT_SET = 0;
    private static final int DIRECTIONAL_LOCK_VERTICAL = 1;
    private static final int DIRECTIONAL_LOCK_HORIZONTAL = 2;
}

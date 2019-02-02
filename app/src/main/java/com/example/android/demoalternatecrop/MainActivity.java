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
    private int initialWidth;
    private int initialHeight;
    private MyImageView mImageView;
    private int mLockDirection = DIRECTIONAL_LOCK_NOT_SET;
    private TextView mDirection;
    private int mTouchSlop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        mImageView = findViewById(R.id.imageView);
        mDirection = findViewById(R.id.direction);

        View verticalLine = findViewById(R.id.verticalLine);
        View horizontalLine = findViewById(R.id.horizontalLine);

        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) verticalLine.getLayoutParams();
        PointF focalPoint = new PointF();
        focalPoint.x = lp.horizontalBias;

        lp = (ConstraintLayout.LayoutParams) horizontalLine.getLayoutParams();
        focalPoint.y = lp.verticalBias;
        mImageView.setFocalPoint(focalPoint);

        if (focalPoint.x == 0.5f && focalPoint.y == 0.5f) {
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        mImageView.setOnTouchListener(new View.OnTouchListener() {
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
                        if (mImageView.getWidth() <= 0) {
                            return false;
                        }
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
                            changeViewSize(v, (int) deltaX, 0);
                            mLastX = event.getX();
                        } else if (mLockDirection == DIRECTIONAL_LOCK_VERTICAL) {
                            changeViewSize(v, 0, (int) deltaY);
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
        findViewById(R.id.layout).post(new Runnable() {
            @Override
            public void run() {
                initialWidth = mImageView.getWidth();
                initialHeight = mImageView.getHeight();
            }
        });
    }

    private void changeViewSize(View view, int w, int h) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = Math.max(0, lp.width + w);
        lp.height = Math.max(0, lp.height + h);
        view.setLayoutParams(lp);
    }

    public void resetSize(View view) {
        ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
        lp.width = initialWidth;
        lp.height = initialHeight;
        mImageView.setLayoutParams(lp);
        mDirection.setText(getString(R.string.not_selected));
    }

    private static final int DIRECTIONAL_LOCK_NOT_SET = 0;
    private static final int DIRECTIONAL_LOCK_VERTICAL = 1;
    private static final int DIRECTIONAL_LOCK_HORIZONTAL = 2;
}

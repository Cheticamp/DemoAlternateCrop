package com.example.android.demoalternatecrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

public class MyImageView extends AppCompatImageView {
    private PointF mFocalPoint = new PointF(0.5f, 0.5f);
    private final int imageRes = R.drawable.test;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        setImageResource(imageRes);
        float imageWidth = ContextCompat.getDrawable(context, imageRes).getIntrinsicWidth();
        float imageHeight = ContextCompat.getDrawable(context, imageRes).getIntrinsicHeight();
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), imageRes);
        BitmapDrawable d = new BitmapDrawable(context.getResources(), b.copy(Bitmap.Config.ARGB_8888, true));
        Canvas c = new Canvas(d.getBitmap());
        Paint p = new Paint();
        p.setColor(context.getResources().getColor(android.R.color.holo_blue_bright));
        p.setStyle(Paint.Style.STROKE);
        int strokeWidth = 60;
        p.setStrokeWidth(strokeWidth);
        if (getScaleType() == ScaleType.MATRIX) {
            // Horizontal line
            c.drawLine(0f, imageHeight * mFocalPoint.y, imageWidth, imageHeight * mFocalPoint.y, p);
            // Vertical line
            c.drawLine(imageWidth * mFocalPoint.x, 0f, imageWidth * mFocalPoint.x, imageHeight, p);
        }
        // Line in horizontal and vertical center
        p.setColor(context.getResources().getColor(android.R.color.white));
        c.drawLine(imageWidth / 2, 0f, imageWidth / 2, imageHeight, p);
        c.drawLine(0f, imageHeight / 2, imageWidth, imageHeight / 2, p);
        setImageDrawable(d);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getScaleType() != ScaleType.MATRIX) {
            return;
        }
        Bitmap b = ((BitmapDrawable) getDrawable()).getBitmap();
        setImageMatrix(prepareMatrix(getViewWidth(), (getViewHeight()),
                                     b.getWidth(), b.getHeight(), mFocalPoint, new Matrix()));
    }

    public void setFocalPoint(PointF focalPoint) {
        mFocalPoint = focalPoint;
        init(this.getContext());
    }

    private Matrix prepareMatrix(float viewWidth, float viewHeight, float mediaWidth, float mediaHeight,
                                 PointF focalPoint, Matrix matrix) {
        float scaleFactorY = viewHeight / mediaHeight;
        float scaleFactor;
        float px;
        float py;
        if (mediaWidth * scaleFactorY >= viewWidth) {
            // Fit height
            scaleFactor = scaleFactorY;
            px = -(mediaWidth * scaleFactor - viewWidth) * focalPoint.x / (1 - scaleFactor);
            py = 0f;
        } else {
            // Fit width
            scaleFactor = viewWidth / mediaWidth;
            px = 0f;
            py = -(mediaHeight * scaleFactor - viewHeight) * focalPoint.y / (1 - scaleFactor);
        }
        matrix.postScale(scaleFactor, scaleFactor, px, py);
        return matrix;
    }

    private float getViewWidth() {
        return (getWidth() - getPaddingStart() - getPaddingEnd());
    }

    private float getViewHeight() {
        return (getHeight() - getPaddingTop() - getPaddingBottom());
    }
}

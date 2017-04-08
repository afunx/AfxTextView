package com.afunx.afxtextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by afunx on 08/04/2017.
 */

public class AfxTextView extends AppCompatTextView {

    private final String TAG = "AfxTextView";

    private final boolean DEBUG = true;

    private Drawable mDrawable;

    // draw bitmap behind textview or not
    private final boolean DRAW_BITMAP_BACKGROUND = true;

    public AfxTextView(Context context) {
        super(context);
        init();
    }

    public AfxTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AfxTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Drawable drawable = getBackground();
        if (drawable instanceof BitmapDrawable) {
            mDrawable = drawable;
            Log.e(TAG, "mDrawable:" + mDrawable);
            setBackgroundDrawable(null);
        }
    }

    private int getDrawableWidth() {
        return mDrawable == null ? 0 : mDrawable.getIntrinsicWidth();
    }

    private int getDrawableHeight() {
        return mDrawable == null ? 0 : mDrawable.getIntrinsicHeight();
    }

    /**
     * onMeasure() related methods
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heighSpectMode = MeasureSpec.getMode(heightMeasureSpec);

        boolean reMeasureWidth = false;
        boolean reMeasureHeight = false;
        int newMeasuredWidth = 0;
        int newMeasuredHeight = 0;

        final int measuredWidthAndState = getMeasuredWidthAndState();
        final int measuredHeightAndState = getMeasuredHeightAndState();
        final int measuredWidthMode = MeasureSpec.getMode(measuredWidthAndState);
        final int measuredHeightMode = MeasureSpec.getMode(measuredHeightAndState);

        // check whether width require reMeasuring
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            final int measuredWidth = getMeasuredWidth();
            final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            final int drawableWidth = getDrawableWidth();

            if (drawableWidth > measuredWidth) {
                newMeasuredWidth = drawableWidth < parentWidth ? drawableWidth : parentWidth;
                reMeasureWidth = true;
                if (DEBUG) {
                    Log.i(TAG, "measureWidth:" + measuredWidth + ",newMeasureWidth:" + newMeasuredWidth);
                }
            }
        }
        // check whether height require reMeasuring
        if (heighSpectMode == MeasureSpec.AT_MOST) {
            final int measuredHeight = getMeasuredHeight();
            final int parenHeight = MeasureSpec.getSize(heightMeasureSpec);
            final int drawableHeight = getDrawableHeight();

            if (drawableHeight > measuredHeight) {
                newMeasuredHeight = drawableHeight < parenHeight ? drawableHeight : parenHeight;
                reMeasureHeight = true;
                if (DEBUG) {
                    Log.i(TAG, "measuredHeight:" + measuredHeight + ",newMeasuredHeight:" + newMeasuredHeight);
                }
            }
        }
        // reMeasure if necessary
        if (reMeasureWidth && reMeasureHeight) {
            final int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(newMeasuredWidth, measuredWidthMode);
            final int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(newMeasuredHeight, measuredHeightMode);
            setMeasuredDimension(newWidthMeasureSpec, newHeightMeasureSpec);
            if (DEBUG) {
                Log.i(TAG, "reMeasureWidth && reMeasureHeight");
            }
        } else if (reMeasureWidth) {
            final int newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(newMeasuredWidth, measuredWidthMode);
            setMeasuredDimension(newWidthMeasureSpec, measuredHeightAndState);
            if (DEBUG) {
                Log.i(TAG, "reMeasureWidth");
            }
        } else if (reMeasureHeight) {
            final int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(newMeasuredHeight, measuredHeightMode);
            setMeasuredDimension(measuredWidthAndState, newHeightMeasureSpec);
            if (DEBUG) {
                Log.i(TAG, "reMeasureHeight");
            }
        }
    }

    /**
     * onDraw() related methods
     */
    private void drawBackground(Drawable drawable, Canvas canvas) {
        // don't forget to setBounds
        setBounds(drawable);

        canvas.save();
        // set where the drawable to be drawn
        translateCanvas(canvas, drawable);

        drawable.draw(canvas);
        canvas.restore();
    }

    private void translateCanvas(Canvas canvas, Drawable drawable) {
        final int drawableWidth = getDrawableWidth();
        final int drawableHeight = getDrawableHeight();
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        final int dx = (paddingLeft - paddingRight) / 2 + (drawableWidth < viewWidth ? (viewWidth - drawableWidth) / 2 : 0);
        final int dy = (paddingTop - paddingBottom) / 2 + (drawableHeight < viewHeight ? (viewHeight - drawableHeight) / 2 : 0);

        canvas.translate(dx, dy);
        if (DEBUG) {
            Log.i(TAG, "viewWidth:" + viewWidth + ",viewHeight:" + viewHeight);
            Log.i(TAG, "paddingLeft:" + paddingLeft + ",paddingRight:" + paddingRight + ",paddingTop:" + paddingTop + ",paddingBottom:" + paddingBottom);
            Log.i(TAG, "dx:" + dx + ",dy:" + dy);
        }
    }

    private void setBounds(Drawable drawable) {
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, drawableWidth, drawableHeight);
        if (DEBUG) {
            Log.i(TAG, "drawableWidth:" + drawableWidth + ",drawableHeight:" + drawableHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable == null) {
            super.onDraw(canvas);
        } else {
            final Drawable drawable = mDrawable;

            if (DRAW_BITMAP_BACKGROUND) {
                if (DEBUG) {
                    Log.i(TAG, "DRAW_BITMAP_BACKGROUND");
                    setBackgroundColor(0xdddddddd);
                }
                drawBackground(drawable, canvas);
                super.onDraw(canvas);
            } else {
                if (DEBUG) {
                    Log.i(TAG, "DRAW_BITMAP_FOREGROUND");
                    setBackgroundColor(0xdddddddd);
                }
                super.onDraw(canvas);
                drawBackground(drawable, canvas);
            }
        }
    }
}

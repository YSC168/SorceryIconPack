package com.sorcerer.sorcery.iconpack.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

/**
 * @description:
 * @author: Sorcerer
 * @date: 2016/2/4 0004
 */
public class QCardView extends CardView {

    private int lastX;
    private int lastY;
    private Scroller mScroller;
    private TouchCallBack mTouchCallBack;

    private boolean mTouchable = false;

    public void setTouchCallBack(TouchCallBack touchCallBack) {
        mTouchCallBack = touchCallBack;
    }

    public QCardView(Context context) {
        this(context, null);
    }

    public QCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
    }

    public boolean isTouchable() {
        return mTouchable;
    }

    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    public interface TouchCallBack {
        void onDown();

        void onUp();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mTouchable) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mTouchCallBack != null) {
                    mTouchCallBack.onDown();
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - lastX;
                int offsetY = y - lastY;

                setTranslationY(getTranslationY() + offsetY);
                setTranslationX(getTranslationX() + offsetX);
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchCallBack != null) {
                    mTouchCallBack.onUp();
                }
                animate().translationX(0)
                        .translationY(0)
                        .setDuration(500)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
                break;
        }
        return true;
    }

}

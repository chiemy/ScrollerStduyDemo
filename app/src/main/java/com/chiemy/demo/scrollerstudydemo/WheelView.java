package com.chiemy.demo.scrollerstudydemo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by chiemy on 15/9/11.
 */
public class WheelView extends ViewGroup{
    /**
     * 字体大小
     */
    private int textSize = 100;
    /**
     * item间距
     */
    private int itemPadding = 20;
    /**
     * item高度
     */
    private int itemHeight = textSize;
    private int visibleItem = 3;

    private int height, width;
    private GestureDetector gestureDetector;
    private Scroller scroller;
    private int maxScrollY;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new MGestureListener());
        scroller = new Scroller(getContext());
        for(int i = 0 ; i < 5; i++){
            TextView textView = new TextView(getContext());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 10);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(Color.RED);
            textView.setText("abc");
            addView(textView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置该ViewGroup的大小
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);


        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
            height = visibleItem*itemHeight + (visibleItem - 1)*itemPadding;
        }

        setMeasuredDimension(width, height);

        int childCount = getChildCount();
        maxScrollY = (childCount - visibleItem)*(itemHeight + itemPadding);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(width, itemHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startTop = 0; // 每个子视图的起始布局坐标
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(0, startTop, getWidth(), startTop + itemHeight);
            startTop += (itemHeight + itemPadding); //校准每个子View的起始布局位置
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);
        if(!result){
            return super.onTouchEvent(event);
        }
        return result;
    }

    @Override
    public void computeScroll() {
        if (!scroller.isFinished()) {
            scroller.computeScrollOffset();
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    private boolean fling;
    private class MGestureListener extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            fling = false;
            scroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            fling = false;
            float distanceToBe = getScrollY() + distanceY;
            if(distanceToBe >= 0 && distanceToBe <= maxScrollY){
                scrollBy(0, (int)distanceY);
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            fling = true;
            int dy;
            if(velocityY < 0){ // 手指向左滑动，下一个
                dy = itemHeight - getScrollY() % itemHeight;
            }else{ // 向左
                dy = -getScrollY() % itemHeight;
            }
            float distanceToBe = getScrollY() + dy;
            if(distanceToBe >= 0 && distanceToBe <= maxScrollY){
                scroller.startScroll(0, getScrollY(), 0, dy, 300);
                postInvalidate();
            }
            return true;
        }
    }
}

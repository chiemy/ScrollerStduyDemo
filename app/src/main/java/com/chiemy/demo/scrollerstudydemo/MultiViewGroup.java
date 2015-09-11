package com.chiemy.demo.scrollerstudydemo;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by chiemy on 15/9/11.
 */
//自定义ViewGroup ， 包含了三个LinearLayout控件，存放在不同的布局位置，通过scrollBy或者scrollTo方法切换
public class MultiViewGroup extends ViewGroup {

    private Context mContext;

    private static String TAG = "MultiViewGroup";
    private int width, height;

    private GestureDetector gestureDetector;

    //Although the velocity calculated by GestureDetector is physically accurate,
    // many developers feel that using this value makes the fling animation too fast.
    // It's common to divide the x and y velocity by a factor of 4 to 8.
    private static final int SCALE = 1;
    private Scroller scroller;
    private int maxScrollX;

    public MultiViewGroup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MultiViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        // 初始化3个 LinearLayout控件
        LinearLayout oneLL = new LinearLayout(mContext);
        oneLL.setBackgroundColor(Color.RED);
        addView(oneLL);

        LinearLayout twoLL = new LinearLayout(mContext);
        twoLL.setBackgroundColor(Color.YELLOW);
        addView(twoLL);

        LinearLayout threeLL = new LinearLayout(mContext);
        threeLL.setBackgroundColor(Color.BLUE);
        addView(threeLL);

        MGestureListener listener = new MGestureListener();
        gestureDetector = new GestureDetector(getContext(), listener);

        scroller = new Scroller(getContext());
    }

    // measure过程
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置该ViewGroup的大小
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        int childCount = getChildCount();
        maxScrollX = (childCount - 1) * width;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(width, height);
        }
    }

    // layout过程
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        int startLeft = 0; // 每个子视图的起始布局坐标
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.layout(startLeft, 0,
                    startLeft + width,
                    height);
            startLeft = startLeft + width ; //校准每个子View的起始布局位置
        }
    }

    @Override
    public void computeScroll() {
        if (!scroller.isFinished()) {
            scroller.computeScrollOffset();
            scrollTo(scroller.getCurrX(), 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    private int currentX;
    private class MGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            scroller.forceFinished(true);
            // 返回true才能接收其他事件
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scrollBy((int) distanceX, 0);
            if(getScrollX() <= 0){
                scrollTo(0, 0);
            }else if(getScrollX() >= maxScrollX){
                scrollTo(maxScrollX, 0);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.fling(getScrollX(), 0, -(int) velocityX / SCALE, 0, 0, maxScrollX, 0, 0);
            postInvalidate();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
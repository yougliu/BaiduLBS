package com.example.helios.baidulbs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Bonus Liu on 1/7/16.
 * email : wumumawl@163.com
 */
public class ScrollListView extends ListView{

    public ScrollListView(Context context) {
        this(context,null);
    }

    public ScrollListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置高度为at most
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

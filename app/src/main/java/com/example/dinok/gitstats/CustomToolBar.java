package com.example.dinok.gitstats;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by dinok on 5/24/2016.
 */
public class CustomToolBar extends Toolbar {

    private TextView mTitleTextView;

    public CustomToolBar(Context context) {
        super(context);
    }

    public CustomToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setmTitleTextView(TextView b) {
        this.mTitleTextView = b;
    }
}

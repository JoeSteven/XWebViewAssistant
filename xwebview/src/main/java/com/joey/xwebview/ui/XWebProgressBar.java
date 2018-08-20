package com.joey.xwebview.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class XWebProgressBar extends ProgressBar implements IWebProgress{
    public XWebProgressBar(Context context) {
        super(context);
    }

    public XWebProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XWebProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        setProgress(progress);
    }

    @Override
    public void onProgressStart() {
        setVisibility(VISIBLE);
    }

    @Override
    public void onProgressDone() {
        setVisibility(GONE);
    }
}

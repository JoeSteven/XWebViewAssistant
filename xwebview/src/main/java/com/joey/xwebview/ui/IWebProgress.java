package com.joey.xwebview.ui;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public interface IWebProgress {
    void onProgressChanged(int progress);

    void onProgressStart();

    void onProgressDone();
}

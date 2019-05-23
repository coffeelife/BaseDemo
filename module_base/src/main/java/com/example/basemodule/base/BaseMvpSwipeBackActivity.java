package com.example.basemodule.base;

import android.os.Bundle;

import com.example.basemodule.base.mvp.BasePresenter;
import com.example.basemodule.views.SlidingLayout;

/**
 * 带滑动返回的mvp基类
 * @author gm
 * @param <P>
 */
public abstract class BaseMvpSwipeBackActivity<P extends BasePresenter> extends BaseMvpActivity<P> {

    protected SlidingLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rootView = new SlidingLayout(this);
        rootView.bindActivity(this);
        super.onCreate(savedInstanceState);
    }

    protected void setSliding(boolean isSliding) {
        rootView.setIsSlide(isSliding);
    }

}

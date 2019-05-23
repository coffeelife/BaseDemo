package com.example.gm.basemain;

import android.os.Bundle;

import com.example.basemodule.base.BaseActivity;
import com.example.basemodule.bean.Event;

public class MainActivity extends BaseActivity {

    @Override
    protected void initImmersionBar(int color) {
        super.initImmersionBar(color);
    }

    @Override
    protected boolean regEvent() {
        return super.regEvent();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView(Bundle bundle) {
        showToast("主页");

    }

    @Override
    protected void onInitialization(Bundle bundle) {

    }

    @Override
    protected void initData(Bundle bundle) {

    }

    @Override
    public void onEventBus(Event event) {
        super.onEventBus(event);
    }


}

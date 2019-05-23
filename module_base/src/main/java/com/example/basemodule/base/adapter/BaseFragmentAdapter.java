package com.example.basemodule.base.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by mguo on 2018/5/5.
 */

public class BaseFragmentAdapter extends FragmentPagerAdapter {

    public static class TabInfo {
        public Class<?> clss;
        public String fTitle;
        public Bundle args;
        public String tag;
    }

    private final Context mContext;
    private List<TabInfo> infoList;

    public BaseFragmentAdapter(Context context, FragmentManager fm, List<TabInfo> infoList) {
        super(fm);
        this.mContext = context;
        this.infoList = infoList;
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo tabInfo = infoList.get(position);
        Fragment instantiate = Fragment.instantiate(mContext, tabInfo.clss.getName(), tabInfo.args);
        return instantiate;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return infoList.get(position).fTitle;
    }
}

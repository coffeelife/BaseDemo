package com.example.basemodule.base;

import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.basemodule.R;
import com.example.basemodule.bean.HorizontalTabTitle;
import com.example.basemodule.widget.PagerSlidingTabStrip;

import java.util.List;

import butterknife.BindView;

/**
 * Describe：带水平选项卡的Activity
 * @author gm
 */

public abstract class HorizontalTabActivity extends BaseMvpActivity{


//    @BindView(R.id.pst_tab)
//    PagerSlidingTabStrip tabStrip;
//
//    @BindView(R.id.vp_list)
//    ViewPager viewPager;
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_horizontal_tab;
//    }
//
//    @CallSuper
//    @Override
//    protected void initView() {
//        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), getTabTitles()) {
//            @Override
//            public int getCount() {
//                return 0;
//            }
//
//            @Override
//            public Fragment getItem(int i) {
//                return null;
//            }
//
//            @Override
//            public BaseFragment getTabFragment() {
//                return HorizontalTabActivity.this.getTabFragment();
//            }
//        });
//        tabStrip.setViewPager(viewPager);
//    }
//
//    protected abstract List<HorizontalTabTitle> getTabTitles();
//
//    protected abstract BaseFragment getTabFragment();


}

package com.example.basemodule.utils;

import android.app.Activity;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by GM on 2018/4/16.
 * Activity管理类
 */
public class ActivityManager {
    public static final String TAG = "ActivityManager";

    public static ActivityManager mActivityManager;
    /**
     * 存放Activity的map
     */
    private CopyOnWriteArrayList<Activity> mActivities = new CopyOnWriteArrayList<Activity>();

    //将构造方法私有化，所以不能通构造方法来初始化ActivityManager
    private ActivityManager() {
    }

    //采用单例模式初始化ActivityManager，使只初始化一次
    public static ActivityManager getInstance() {
        if (mActivityManager == null) {
            mActivityManager = new ActivityManager();
        }
        return mActivityManager;
    }

    //添加activity
    public void addActivity(Activity activity) {
        if (!mActivities.contains(activity)) {
            mActivities.add(activity);
        }
    }

    //关闭指定的Activity
    public void removeActivity(Activity activity) {
        if (activity != null) {
            if (mActivities.contains(activity)) {
                mActivities.remove(activity);
            }
            activity.finish();
            activity = null;
        }
    }

    //将activity全部关闭掉
    public void clearAll() {
        for (Activity activity : mActivities) {
            activity.finish();
        }
    }

    //将activity全部关闭掉,除掉Aty
    public void clearAtyOther(String atyName) {
        for (Activity activity : mActivities) {
            if (activity == null) {
                continue;
            }
            if (activity.getClass().getSimpleName().equals(atyName)) {
                continue;
            }
            activity.finish();
        }
    }

    //将activity全部关闭掉,除掉LoginActivity
    public void clearLoginOther() {
        try {
            for (Activity activity : mActivities) {
                if (activity == null) {
                    continue;
                }
                if (activity.getClass().getSimpleName().equals("LoginActivity")) {
                    continue;
                }
                activity.finish();
            }
        } catch (Exception e) {

        }
    }

    //将activity全部关闭掉,除掉LoginActivity
    public void clearActivity(String atyName) {
        for (Activity activity : mActivities) {
            if (activity == null) {
                continue;
            }
            if (activity.getClass().getSimpleName().equals(atyName)) {
                activity.finish();
            }
        }
    }

    //判断某个activity是否存在
    public boolean isExistActivity(String atyName) {
        boolean isExist = false;
        for (Activity activity : mActivities) {
            if (activity == null) {
                continue;
            }
            if (activity.getClass().getSimpleName().equals(atyName)) {
                isExist = true;
            }
            continue;
        }
        return isExist;
    }

    public int existAtyNum(String atyName) {
        int num = 0;
        for (Activity activity : mActivities) {
            if (activity.getClass().getSimpleName().equals(atyName)) {
                num++;
            }
        }
        return num;
    }

}
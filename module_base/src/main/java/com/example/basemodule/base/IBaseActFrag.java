package com.example.basemodule.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * @author ccx
 */
public interface IBaseActFrag {

    void gStartActivity(Intent intent);

    void gStartActivity(Class<? extends Activity> cls);

    /**
     * 打开新的Activity
     */
    void gStartActivity(Class<? extends Activity> cls, Bundle bundle);

    void gStartShareActivity(String title, String shareContent);

    void gStartImageShare(String shareContent, Uri uri);

    /**
     * 打开新的Activity for result
     */
    void gStartActivityForResult(Class<? extends Activity> cls, Bundle bundle, int requestCode);

    /**
     * 带结果返回上一个activity， 配合qStartActivityForResult使用
     */
    void gBackForResult(int resultCode, Bundle bundle);

    /**
     * 回到之前的Activity
     */
    void gBackToActivity(Class<? extends Activity> cls, Bundle bundle);

    Context getContext();

}

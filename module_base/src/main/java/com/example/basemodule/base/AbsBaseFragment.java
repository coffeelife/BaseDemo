package com.example.basemodule.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.basemodule.R;

/**
 * Created by ccx on 2018/07/23
 */
public abstract class AbsBaseFragment extends Fragment implements IBaseActFrag {
    @Override
    public void gStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void gStartActivity(Class<? extends Activity> cls) {
        gStartActivity(cls, null);
    }

    /* 打开新的Activity */
    @Override
    public void gStartActivity(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(getActivity(), cls);
        startActivity(intent);
    }

    @Override
    public void gStartShareActivity(String title, String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (title != null) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        startActivity(Intent.createChooser(intent,
            getString(R.string.share_message)));
    }

    @Override
    public void gStartImageShare(String shareContent, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("headpic/*");
        if (uri == null) {
            gStartShareActivity(null, shareContent);
            return;
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.putExtra("sms_body", shareContent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent,
            getString(R.string.share_message)));
    }

    /* 打开新的Activity for result */
    @Override
    public void gStartActivityForResult(Class<? extends Activity> cls,
                                        Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(getActivity(), cls);
        startActivityForResult(intent, requestCode);
    }

    /* 带结果返回上一个activity， 配合gStartActivityForResult使用 */
    @Override
    public void gBackForResult(int resultCode, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }

    /* 回到之前的Activity */
    @Override
    public void gBackToActivity(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(getActivity(), cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /* 根据url跳转Activity */
    public void gStartActivity(String url, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return (AbsBaseActivity) getActivity();
    }
}

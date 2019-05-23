package com.example.basemodule.views.jswebview;

import android.view.View;
import android.widget.TextView;

import com.example.basemodule.widget.NumberProgressBar;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;


/**
 * Class description
 *
 * @author YEZHENNAN220
 * @date 2016-07-08 14:05
 */

public class CustomWebChromeClient extends WebChromeClient {


    private NumberProgressBar mProgressBar;
    private TextView title;
    private final static int DEF = 95;

    public CustomWebChromeClient(NumberProgressBar progressBar, TextView title) {
        this.mProgressBar = progressBar;
        this.title = title;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress >= DEF) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            if (mProgressBar.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mProgressBar.setProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(WebView webView, String s) {
        super.onReceivedTitle(webView, s);
        if (!webView.getTitle().contains("http")&&!webView.getTitle().contains(":"))
            this.title.setText(webView.getTitle());
    }

}

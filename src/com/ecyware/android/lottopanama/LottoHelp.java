package com.ecyware.android.lottopanama;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class LottoHelp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lotto_help);

        WebView webView = (WebView) this.findViewById(R.id.helpView);
        webView.loadUrl("file:///android_asset/www/help.html");
    }

}

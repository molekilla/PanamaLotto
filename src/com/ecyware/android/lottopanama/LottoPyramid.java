package com.ecyware.android.lottopanama;

import com.ecyware.android.lottopanama.logic.LottoService;
import com.ecyware.android.lottopanama.ui.LottoPyramidView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.util.List;


public class LottoPyramid extends Activity {

    ProgressDialog dialog = null;
    private ResponseReceiver receiver;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lotto_piramide);

        // Show progress bar
        dialog = ProgressDialog.show(this, "", "Generando datos...", true);

        // register receiver
        IntentFilter intentFilter = new IntentFilter(LottoService.ACTION_PYRAMID_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        receiver = new ResponseReceiver();
        registerReceiver(receiver, intentFilter);

        // request data
        Intent pyramidDataIntent = new Intent(this, LottoService.class);
        pyramidDataIntent.setAction(LottoService.ACTION_PYRAMID);
        startService(pyramidDataIntent);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            Bundle bundle = intent.getBundleExtra(LottoService.PYRAMID_BUNDLE);
            if (bundle != null)
            {
                // ActionBarView actionBar = (ActionBarView)
                // findViewById(R.id.lottoDetailHeader);
                List<String> values = bundle.getStringArrayList(LottoService.PYRAMID_VALUES);
                RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.piramideLayout);

                int w = rootLayout.getWidth();
                int h = (int) rootLayout.getHeight() / 2;
                LottoPyramidView pyramidView = new LottoPyramidView(context, values);

                Bitmap result = Bitmap.createBitmap(w,
                        h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(result);
                pyramidView.draw(canvas);

                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);

                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
                layoutParams.topMargin = 80 + rootLayout.getTop();
                pyramidView.setLayoutParams(layoutParams);

                rootLayout.addView(pyramidView);
            }

            dialog.dismiss();
        }
    }
}

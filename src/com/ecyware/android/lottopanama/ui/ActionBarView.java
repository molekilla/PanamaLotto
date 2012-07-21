package com.ecyware.android.lottopanama.ui;

import com.ecyware.android.lottopanama.Dashboard;
import com.ecyware.android.lottopanama.R;
import com.ecyware.android.lottopanama.logic.LottoService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionBarView extends LinearLayout {

    private ResponseReceiver receiver;

    public ActionBarView(Context context, AttributeSet attrs)
    {

        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.lotto_actionbar, this, true);
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.ActionBar, 0, 0);

        String titleText = values.getString(R.styleable.ActionBar_titleText);
        if (titleText != null)
        {
            TextView title = (TextView) this.findViewById(R.id.lottoActionBarText);
            title.setText(titleText);
        }

        TextView homeButton = (TextView) this.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intentLastLotto = new Intent(v.getContext(), Dashboard.class);
                        v.getContext().startActivity(intentLastLotto);
                    }
                }
                );

        ImageButton shareButton = (ImageButton) this.findViewById(R.id.lottoShare);
        shareButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        sendToShare(v.getContext());

                    }
                });
        values.recycle();

        // register receiver
        IntentFilter intentFilter = new IntentFilter(LottoService.ACTION_SHARE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        receiver = new ResponseReceiver();
        context.registerReceiver(receiver, intentFilter);
    }

    public void sendToShare(Context c)
    {
        // request data
        Intent pyramidDataIntent = new Intent(c, LottoService.class);
        pyramidDataIntent.setAction(LottoService.ACTION_SHARE);
        c.startService(pyramidDataIntent);
    }

    public void share(Context c, String subject, String text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        c.startActivity(Intent.createChooser(intent, "Share"));
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            String content = intent.getStringExtra(Intent.EXTRA_TEXT);
            share(context, title, content);
        }
    }
}

package com.ecyware.android.lottopanama;

import com.ecyware.android.lottopanama.logic.ApplicationPreferences;
import com.ecyware.android.lottopanama.logic.LottoNotificationService;
import com.ecyware.android.lottopanama.logic.LottoService;

import roboguice.activity.RoboActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Date;

public class Dashboard extends RoboActivity {
	
    ProgressDialog dialog = null;
    private ResponseReceiver receiver;
    private PendingIntent alarmSender;
    private ApplicationPreferences appPreferences;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an IntentSender that will launch our service, to be scheduled
        // with the alarm manager.
        alarmSender = PendingIntent.getService(this,
                0, new Intent(this, LottoNotificationService.class), 0);

        setContentView(R.layout.dashboard);
        appPreferences = new ApplicationPreferences(this.getApplicationContext());
        ImageButton shareButton = (ImageButton) this.findViewById(R.id.lottoDetailHeader)
                .findViewById(R.id.lottoShare);
        shareButton.setVisibility(8);
        ImageButton refreshButton = (ImageButton) this.findViewById(R.id.lottoDetailHeader)
                .findViewById(R.id.lottoRefreshData);
        refreshButton.setVisibility(0);
        refreshButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        requestRefreshData();
                    }
                }
                );
    }
    
    private void requestRefreshData()
    {
        // Show progress bar
        dialog = ProgressDialog.show(this, "", "Actualizando datos...", true);

        // register receiver
        IntentFilter intentFilter = new IntentFilter(LottoService.ACTION_REFRESH_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, intentFilter);

        // request data
        Intent dataIntent = new Intent(this, LottoService.class);
        dataIntent.setAction(LottoService.ACTION_REFRESH);
        startService(dataIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.setNotificationAlarm();
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

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lotto_options_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = (MenuItem) menu.findItem(R.id.optionAlert);
        if (this.appPreferences.getLottoAlertState())
        {
            item.setTitle(this.getString(R.string.alertCanInactivate));
        } else {
            item.setTitle(this.getString(R.string.alertCanActivate));
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.optionAlert:
                if (this.appPreferences.getLottoAlertState())
                {
                    cancelNotificationAlarm();
                } else {
                    setNotificationAlarm();
                }

                this.appPreferences.setLottoAlertState();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cancelNotificationAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(alarmSender);
        Toast.makeText(this, R.string.notification_alert_off_text, Toast.LENGTH_LONG);
    }
    private void setNotificationAlarm()
    {
        Date startInterval = new Date();
        startInterval.setTime(CalendarHelper.getNotificationCalendarDate(14, 25).getTimeInMillis());

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(alarmSender);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                startInterval.getTime(),
                24 * 60 * 60 * 1000,
                alarmSender);
        Toast.makeText(this, R.string.notification_alert_on_text, Toast.LENGTH_LONG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //Bundle extras = intent.getExtras();
        //switch(requestCode) {
          //  case LOTTO_VIEW:
                //String title = extras.getString(NotesDbAdapter.KEY_TITLE);
                //String body = extras.getString(NotesDbAdapter.KEY_BODY);
                //mDbHelper.createNote(title, body);
                //fillData();
            //    break;

        //}
     
    }
    public void onLastLottery(View v) {
    	Intent intentLastLotto = new Intent(this, LottoDetail.class);
    	this.startActivityForResult(intentLastLotto, Constants.LOTTO_VIEW);
    }

    public void onLastMonthLottery(View v) {
    	Intent intentLottoHistory = new Intent(this, LottoHistory.class);
    	this.startActivityForResult(intentLottoHistory, Constants.LOTTO_HISTORY_VIEW);
    }

    public void onActionThreeClick(View v) {
        Intent intentPyramid = new Intent(this, LottoPyramid.class);
        this.startActivityForResult(intentPyramid, Constants.LOTTO_PYRAMID);
    }

    public void onActionFourClick(View v) {
        Intent intentHelp = new Intent(this, LottoHelp.class);
        this.startActivityForResult(intentHelp, Constants.LOTTO_WEB_HELP);
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            dialog.dismiss();
        }
    }
}
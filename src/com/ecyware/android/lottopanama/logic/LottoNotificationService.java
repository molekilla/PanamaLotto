package com.ecyware.android.lottopanama.logic;

import com.ecyware.android.lottopanama.LottoDetail;
import com.ecyware.android.lottopanama.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public class LottoNotificationService extends Service {

    NotificationManager mNM;
    protected ApplicationPreferences appPreferences;
    String lastHash;

    @Override
    public void onCreate() {
        appPreferences = new ApplicationPreferences(this.getApplicationContext());
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        lastHash = this.appPreferences.getLastWebHash();
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        Thread thr = new Thread(null, hasLottoUpdateTask, "lotto.notificationservice");
        thr.start();

    }

    Runnable hasLottoUpdateTask = new Runnable() {
        public void run() {

            synchronized (mBinder) {

                LottoHtmlContext lottoHtmlContext = LottoServiceHelper
                        .getLottoHtml(LottoService.LOTTO_URL);
                if (lottoHtmlContext.hasUpdate(lastHash))
                {
                    // show the icon in the status bar
                    showNotification();
                }
            }
            // Done with our work... stop the service!
            LottoNotificationService.this.stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        // Cancel the notification -- we use the same ID that we had used to
        // start it
        // mNM.cancel(R.string.lotto_notification_key);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the
        // expanded notification
        CharSequence text = getText(R.string.lotto_notification_text);
        CharSequence label = getText(R.string.lotto_notification_label);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.lotto_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LottoDetail.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this.getApplicationContext(), label, text, contentIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Send the notification.
        // We use a layout id because it is a unique number. We use it later to
        // cancel.
        mNM.notify(R.string.lotto_notification_key, notification);
    }

    /**
     * This is the object that receives interactions from clients. See
     * RemoteService for a more complete example.
     */
    private final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,
                int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };
}

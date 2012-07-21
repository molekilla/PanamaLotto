package com.ecyware.android.lottopanama.logic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class ApplicationPreferences {

	public static final String LOTTO_SETTINGS = "LottoSettings";
	private SharedPreferences appSharedPreferences;
	private String LOTTO_LAST_REFRESHED = "LottoLastUpdated";
	private String LOTTO_HTML = "LottoHtml";
    private String LOTTO_LAST_ACCESS = "LottoLastAccess";

    private String LOTTO_OPTIONS_ALERT = "LottoAlertState";
	
	public ApplicationPreferences(Context context)
	{
		this.appSharedPreferences = context.getSharedPreferences(LOTTO_SETTINGS, Activity.MODE_PRIVATE);
	}
	

    public boolean getLottoAlertState() {
        return appSharedPreferences.getBoolean(LOTTO_OPTIONS_ALERT, false);
    }

    public void setLottoAlertState() {

        // 1 - Active
        // 0 - Inactive, default is inactive
        SharedPreferences.Editor editor = appSharedPreferences.edit();
        if (getLottoAlertState())
        {
            editor.putBoolean(LOTTO_OPTIONS_ALERT, false);
        } else {
            editor.putBoolean(LOTTO_OPTIONS_ALERT, true);
        }
        editor.commit();
    }
	public Date getLastDatabaseUpdate() {
		// Read shared preferences
		float lastUpdated = appSharedPreferences.getFloat(LOTTO_LAST_REFRESHED, 0);
		Date lastUpdatedDate = new Date();
		if (lastUpdated != 0) {
			lastUpdatedDate.setTime((long) lastUpdated);
		} else {
			lastUpdatedDate = null;
		}
		return lastUpdatedDate;
	}

	public void saveLastDatabaseUpdate() {
		// Read shared preferences
		SharedPreferences.Editor editor = appSharedPreferences.edit();
		editor.putFloat(LOTTO_LAST_REFRESHED,
				Long.valueOf(System.currentTimeMillis()));
		editor.commit();

	}

	public String getLastWebHash() {
		// Read shared preferences
		return appSharedPreferences.getString(LOTTO_HTML, "");
	}

	public void saveLastWebHash(String hash) {
		// Read shared preferences
		SharedPreferences.Editor editor = appSharedPreferences.edit();
		editor.putString(LOTTO_HTML, hash);
		editor.commit();

	}

    public boolean hasLastAccessInMinutes(int minutes)
    {
        boolean response = false;

        // Read shared preferences
        long lastAccess = (long) appSharedPreferences.getFloat(LOTTO_LAST_ACCESS, 0);

        if (lastAccess != 0) {
            long current = System.currentTimeMillis();
            long diff = Math.abs(current - lastAccess);
            long diffInSeconds = (diff / 1000);
            if ((minutes * 60) >= diffInSeconds)
            {
                response = true;
            }
        }
        return response;
    }
    public void saveLastAccess() {
        // Read shared preferences
        SharedPreferences.Editor editor = appSharedPreferences.edit();
        editor.putFloat(LOTTO_LAST_ACCESS,
                Long.valueOf(System.currentTimeMillis()));
        editor.commit();
    }

}

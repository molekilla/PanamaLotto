package com.ecyware.android.lottopanama.logic;

import com.ecyware.android.lottopanama.CalendarHelper;
import com.ecyware.android.lottopanama.lottoprovider.LottoProviderMetadata.LottoTableMetadata;
import com.google.inject.Singleton;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class LottoService
        extends IntentService
        implements ILottoDataService {

    // Intent actions
    public static final String ACTION_SHARE = "com.ecyware.android.lottopanama.intent.LOTTO_SHARE";
    public static final String ACTION_SHARE_RESPONSE = "com.ecyware.android.lottopanama.intent.LOTTO_SHARE_RESPONSE";
    public static final String ACTION_REFRESH = "com.ecyware.android.lottopanama.intent.LOTTO_REFRESH";
    public static final String ACTION_REFRESH_RESPONSE = "com.ecyware.android.lottopanama.intent.LOTTO_REFRESH_RESPONSE";
    public static final String ACTION_DETAIL = "com.ecyware.android.lottopanama.intent.LOTTO_DETAIL";
    public static final String ACTION_DETAIL_RESPONSE = "com.ecyware.android.lottopanama.intent.LOTTO_DETAIL_RESPONSE";
    public static final String ACTION_HISTORY = "com.ecyware.android.lottopanama.intent.LOTTO_HISTORY";
    public static final String ACTION_HISTORY_RESPONSE = "com.ecyware.android.lottopanama.intent.LOTTO_HISTORY_RESPONSE";
    public static final String ACTION_PYRAMID = "com.ecyware.android.lottopanama.intent.LOTTO_PYRAMID";
    public static final String ACTION_PYRAMID_RESPONSE = "com.ecyware.android.lottopanama.intent.LOTTO_PYRAMID_RESPONSE";
	// Binder given to clients
	private final IBinder binder = new LottoServiceBinder();
	protected ApplicationPreferences appPreferences;
	public static final String LOTTO_URL = "http://www.lnb.gob.pa/sitio/numerosjugados.php";
    public static final String LOTTO_HISTORY_URL = "http://www.lnb.gob.pa/sitio/numerosjugados.php?tiposorteo=T&ano=%s&meses=%s&Consultar=consulta";
    public static final String PYRAMID_BUNDLE = "lottoPyramidBundle";
    public static final String PYRAMID_VALUES = "lottoPyramidValues";

	
    public LottoService() {
        super("LottoService");
    }



   
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Intent response = new Intent();
        response.addCategory(Intent.CATEGORY_DEFAULT);

        appPreferences = new ApplicationPreferences(this.getApplicationContext());

        if (action.compareTo(ACTION_SHARE) == 0)
        {
            // TODO: Can't serialize, use Jackson JSON or something light
            List<LottoItem> lottoItems = this.readCurrentLotto();
            LottoItem lottoItem = lottoItems.get(lottoItems.size() - 1);
            response.setAction(ACTION_SHARE_RESPONSE);
            response.putExtra(Intent.EXTRA_SUBJECT, lottoItem.getLottoHeader(this));
            response.putExtra(Intent.EXTRA_TEXT, lottoItem.getLottoDetail());
            sendBroadcast(response);
        }
        else if (action.compareTo(ACTION_REFRESH) == 0)
        {
            response.setAction(ACTION_REFRESH_RESPONSE);
            this.refreshLottoData(4);
            sendBroadcast(response);
        }
        else if (action.compareTo(ACTION_PYRAMID) == 0)
        {
            response.setAction(ACTION_PYRAMID_RESPONSE);

            List<Integer> projectedItems = this.generatePyramidProjection();
            List<String> projectedStringItems = new ArrayList<String>();
            projectedStringItems.add(String.valueOf(projectedItems.get(0)));
            projectedStringItems.add(String.valueOf(projectedItems.get(1)));
            projectedStringItems.add(String.valueOf(projectedItems.get(2)));
            Bundle pyramidValues = new Bundle();
            pyramidValues.putStringArrayList(PYRAMID_VALUES,
                    (ArrayList<String>) projectedStringItems);
            response.putExtra(PYRAMID_BUNDLE, pyramidValues);

            sendBroadcast(response);
        }

    }


	public class LottoServiceBinder extends Binder {
		public LottoService getService() {
			return LottoService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
        if (appPreferences == null)
            appPreferences = new ApplicationPreferences(this.getApplicationContext());
		return binder;
	}


    public void refreshLottoData(int months)
	{
        // remove all records
        getContentResolver().delete(LottoTableMetadata.CONTENT_URI, null, null);
        // include current month , add plus one
        int month = CalendarHelper.currentMonth() + 1;
        int year = CalendarHelper.currentYear();
        
        int i = 0;
        int temp = 0;
        do
        {
            temp = month - i;
            if (temp < 1)
            {
                if (temp == 1)
                {
                    temp = 0;
                }
                year = year - 1;
                temp = 12 + temp;
            }
            
            addLottoMonth(year, temp - 1);
            
            i++;
        } while (i < months);
        
          
	}

    public List<Integer> generatePyramidProjection()
    {
        List<Integer> list = new ArrayList<Integer>();
        LottoItem currentLottoItem = readCurrentLotto().get(0);
        String numbers = currentLottoItem.getFirstNumber() +
                currentLottoItem.getSecondNumber() +
                currentLottoItem.getThirdNumber();

        int first = Integer.valueOf(numbers.substring(0, 1));
        int last = Integer.valueOf(numbers.substring(numbers.length() - 1, numbers.length()));
        int bottom = 0;
        String temp = String.valueOf(first);

        StringBuilder buffer = new StringBuilder();
        boolean doExit = false;
        do
        {

            for (int i = 1; i < numbers.length(); i++)
            {
                // sum
                String nextChar = String.valueOf(numbers.charAt(i));
                int sumInt = Integer.valueOf(temp) + (int) Integer.valueOf(nextChar);
                String sum = String.valueOf(sumInt);
                if (sum.length() == 2)
                {
                    buffer.append(sum.charAt(1));
                }
                else
                {
                    buffer.append(sum);
                }
                temp = String.valueOf(numbers.charAt(i));
            }

            doExit = buffer.toString().length() > 1;
            numbers = buffer.toString();
            temp = String.valueOf(numbers.charAt(0));
            buffer = new StringBuilder();
        } while (doExit);

        bottom = Integer.valueOf(numbers);
        list.add(first);
        list.add(last);
        list.add(bottom);
        return list;
    }
	public List<LottoItem> readLottoByDate(String id) {
		Cursor cursor = getContentResolver().query(
				LottoTableMetadata.CONTENT_URI, null,
				LottoTableMetadata.LOTTO_DATE + "=?", new String[] { id },
				LottoTableMetadata.LOTTO_TYPED_DATE + " DESC");

		LottoItems lottoItems = new LottoItems();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				lottoItems.addFromCursor(cursor);
			}
		}
		cursor.close();
		return lottoItems;

	}
	public List<LottoItem> readCurrentLotto() {
		return readLottoData(1);
	}
	public List<LottoItem> readLottoHistory() {
		return readLottoData(-1);
	}
    public List<LottoItem> addLottoMonth(int year, int month)
    {
        String url = "";
        if (String.valueOf(month).length() == 2)
        {
            url = String
                    .format(LottoService.LOTTO_HISTORY_URL, String.valueOf(year),
                            String.valueOf(month));
        } else {
            url = String.format(LottoService.LOTTO_HISTORY_URL, String.valueOf(year),
                    "0" + String.valueOf(month));
        }
        List<LottoItem> lottoNumbers = LottoServiceHelper.readLottoWebData(url, null);
        syncLottoNumbers(lottoNumbers);
        this.appPreferences.saveLastDatabaseUpdate();
        return lottoNumbers;
    }

	private List<LottoItem> readLottoData(int requestedSize) {
		
		LottoItems lottoItems = new LottoItems();

        boolean useDatabase = true;
        LottoHtmlContext lottoHtmlContext = null;

        if (!this.appPreferences.hasLastAccessInMinutes(15))
        {
            String lastHash = this.appPreferences.getLastWebHash();
            lottoHtmlContext = LottoServiceHelper.getLottoHtml(LOTTO_URL);
            useDatabase = !lottoHtmlContext.hasUpdate(lastHash);
        }

        if (useDatabase) {

			// Read content provider
			Cursor cursor = getContentResolver().query(
					LottoTableMetadata.CONTENT_URI, null, null, null,
					LottoTableMetadata.LOTTO_TYPED_DATE + " DESC");
            boolean isDataFound = false;
            if (cursor != null) {
				do {
					isDataFound = cursor.moveToNext();
					if (isDataFound) {
						lottoItems.addFromCursor(cursor);
					} 
				} while (isDataFound && lottoItems.size() != requestedSize);
			}
            if (!isDataFound && lottoItems.size() == 0)
            {
                lottoItems = this.readAndSyncWebData(lottoHtmlContext, requestedSize);
            }
			cursor.close();
		} else {
            lottoItems = this.readAndSyncWebData(lottoHtmlContext, requestedSize);
		}

        this.appPreferences.saveLastAccess();
        if (lottoHtmlContext != null)
        {
            this.appPreferences.saveLastWebHash(lottoHtmlContext.getHash());
        }
        return lottoItems;
	}

    private LottoItems readAndSyncWebData(LottoHtmlContext lottoHtmlContext, int requestedSize)
    {
        LottoItems lottoItems = new LottoItems();
        List<LottoItem> lottoNumbers = LottoServiceHelper.readLottoWebData(null,
                lottoHtmlContext.getHtml());
        if (lottoNumbers.size() == 0)
        {
            int month = CalendarHelper.currentMonth();
            int year = CalendarHelper.currentYear();
            lottoNumbers = addLottoMonth(year, month - 1);
        } else {
            syncLottoNumbers(lottoNumbers);
        }

        // if only one item
        if (requestedSize == 1) {
            LottoItem currentItem = lottoNumbers.get(lottoNumbers.size() - 1);
            lottoItems.add(currentItem);
        } else {
            Collections.reverse(lottoNumbers);
            lottoItems.addAll(lottoNumbers);
        }
        this.appPreferences.saveLastDatabaseUpdate();

        return lottoItems;
    }
	private void syncLottoNumbers(List<LottoItem> lottoNumbers) {
		for (LottoItem item : lottoNumbers) {
			String dateInMilliseconds = Long.valueOf(item.getLottoDateTyped().getTime()).toString();
			Cursor cursor = getContentResolver().query(
					LottoTableMetadata.CONTENT_URI, null,
					LottoTableMetadata.LOTTO_TYPED_DATE + "=?",
					new String[] { dateInMilliseconds },
					LottoTableMetadata.LOTTO_TYPED_DATE + " DESC");

			if (cursor != null) {
				if (cursor.moveToFirst() == false) {
					ContentValues values = new ContentValues();
					values.put(LottoTableMetadata.FIRST_PRIZE, item.getFirstNumber());
					values.put(LottoTableMetadata.SECOND_PRIZE, item.getSecondNumber());
					values.put(LottoTableMetadata.THIRD_PRIZE, item.getThirdNumber());
					values.put(LottoTableMetadata.FOLIO, item.getFolio());
					values.put(LottoTableMetadata.LETRAS, item.getLetters());
					values.put(LottoTableMetadata.LOTTO_DATE, item.getLottoDate());
					values.put(LottoTableMetadata.LOTTO_TYPE, item.getHeader());
					values.put(LottoTableMetadata.LOTTO_TYPED_DATE, Long.valueOf(item.getLottoDateTyped().getTime()));
					values.put(LottoTableMetadata.LOTTO_YEARMONTH, item.getLottoYearMonth());
					values.put(LottoTableMetadata.SERIE, item.getSerie());
					getContentResolver().insert(LottoTableMetadata.CONTENT_URI, values);
				}
			}
			cursor.close();
		}
	}

}

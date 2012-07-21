package com.ecyware.android.lottopanama.lottoprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	
	private static final String TAG = "lotto database helper";

	DatabaseHelper(Context context) {
		super(context, LottoProviderMetadata.DATABASE_NAME, null, LottoProviderMetadata.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    db.execSQL("CREATE TABLE " + LottoProviderMetadata.LottoTableMetadata.TABLE_NAME + " ("
	            + LottoProviderMetadata.LottoTableMetadata._ID  
	            + " INTEGER PRIMARY KEY," 
	            + LottoProviderMetadata.LottoTableMetadata.FIRST_PRIZE + " TEXT," 
	            + LottoProviderMetadata.LottoTableMetadata.SECOND_PRIZE + " TEXT," 
	            + LottoProviderMetadata.LottoTableMetadata.THIRD_PRIZE + " TEXT," 
	            + LottoProviderMetadata.LottoTableMetadata.FOLIO + " TEXT," 
	            + LottoProviderMetadata.LottoTableMetadata.LETRAS + " TEXT," 
	            + LottoProviderMetadata.LottoTableMetadata.LOTTO_TYPE + " TEXT,"
	            + LottoProviderMetadata.LottoTableMetadata.SERIE + " TEXT,"
	            + LottoProviderMetadata.LottoTableMetadata.LOTTO_YEARMONTH + " TEXT,"
	            + LottoProviderMetadata.LottoTableMetadata.LOTTO_DATE + " TEXT,"
	            + LottoProviderMetadata.LottoTableMetadata.LOTTO_TYPED_DATE + " INTEGER,"
	            + LottoProviderMetadata.LottoTableMetadata.CREATED_DATE + " INTEGER" 
	            + ");"); 

	}

	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " 
                + newVersion + ", which will destroy all old data"); 
        db.execSQL("DROP TABLE IF EXISTS " + LottoProviderMetadata.LottoTableMetadata.TABLE_NAME); 
        onCreate(db); 
    } 

}

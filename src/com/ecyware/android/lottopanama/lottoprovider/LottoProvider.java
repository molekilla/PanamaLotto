package com.ecyware.android.lottopanama.lottoprovider;

import com.ecyware.android.lottopanama.lottoprovider.LottoProviderMetadata.LottoTableMetadata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

public class LottoProvider extends ContentProvider {

	private DatabaseHelper databaseHelper;
	
	// Create a Projection Map for Columns  
    private static HashMap<String, String> lottoProjectionMap; 
    static  
    { 
    	lottoProjectionMap = new HashMap<String, String>(); 
    	lottoProjectionMap.put(LottoTableMetadata._ID, LottoTableMetadata._ID); 
        
       // first, second, third, letras, folio, serie, lottoType, lotto date, lotto year month
    	lottoProjectionMap.put(LottoTableMetadata.FIRST_PRIZE, LottoTableMetadata.FIRST_PRIZE); 
    	lottoProjectionMap.put(LottoTableMetadata.SECOND_PRIZE, LottoTableMetadata.SECOND_PRIZE);
    	lottoProjectionMap.put(LottoTableMetadata.THIRD_PRIZE, LottoTableMetadata.THIRD_PRIZE); 
    	lottoProjectionMap.put(LottoTableMetadata.FOLIO, LottoTableMetadata.FOLIO);
    	lottoProjectionMap.put(LottoTableMetadata.LETRAS, LottoTableMetadata.LETRAS); 
    	lottoProjectionMap.put(LottoTableMetadata.LOTTO_TYPE, LottoTableMetadata.LOTTO_TYPE);
    	lottoProjectionMap.put(LottoTableMetadata.SERIE, LottoTableMetadata.SERIE);
    	lottoProjectionMap.put(LottoTableMetadata.LOTTO_DATE, LottoTableMetadata.LOTTO_DATE);
    	lottoProjectionMap.put(LottoTableMetadata.LOTTO_YEARMONTH, LottoTableMetadata.LOTTO_YEARMONTH);
        
       // created date, lotto date 
       lottoProjectionMap.put(LottoTableMetadata.CREATED_DATE, LottoTableMetadata.CREATED_DATE); 
       lottoProjectionMap.put(LottoTableMetadata.LOTTO_TYPED_DATE, LottoTableMetadata.LOTTO_TYPED_DATE); 
    } 
 
    // Provide a mechanism to identify all the incoming uri patterns. 
    private static final UriMatcher uriMatcher; 
    private static final int INCOMING_LOTTO_MONTH_URI_INDICATOR = 1; 
    private static final int INCOMING_LOTTO_SINGLE_URI_INDICATOR = 2; 
    private static final int INCOMING_LOTTO_TABLE = 3;
    
    static 
    { 
    	uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); 
    	uriMatcher.addURI(LottoProviderMetadata.AUTHORITY, "lottoNumbers/month/#" 
                               ,INCOMING_LOTTO_MONTH_URI_INDICATOR); 
    	uriMatcher.addURI(LottoProviderMetadata.AUTHORITY, "lottoNumbers/date/#", 
                              INCOMING_LOTTO_SINGLE_URI_INDICATOR); 
    	uriMatcher.addURI(LottoProviderMetadata.AUTHORITY, "lottoNumbers",INCOMING_LOTTO_TABLE);
  } 
    
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
	    int count = 0; 
	    switch (uriMatcher.match(uri)) { 
            case INCOMING_LOTTO_TABLE:
                count = db.delete(LottoTableMetadata.TABLE_NAME, where, whereArgs);
	        break; 
            // case INCOMING_LOTTO_SINGLE_URI_INDICATOR:
//	        String rowId = uri.getPathSegments().get(1); 
//	        count = db.delete(LottoTableMetadata.TABLE_NAME, values, LottoTableMetadata._ID + "=" + rowId 
//	              + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs); 
            // break;
	    default: 
	        throw new IllegalArgumentException("Unknown URI " + uri); 
	    } 
	
	    getContext().getContentResolver().notifyChange(uri, null); 
	    return count; 
	}

	@Override
	public String getType(Uri uri) {
		  switch (uriMatcher.match(uri)) { 
		  case INCOMING_LOTTO_MONTH_URI_INDICATOR: 
		      return LottoTableMetadata.CONTENT_MONTH_ITEM_TYPE; 
		  case INCOMING_LOTTO_SINGLE_URI_INDICATOR: 
		      return LottoTableMetadata.CONTENT_ITEM_TYPE; 
		  case INCOMING_LOTTO_TABLE:
			  return LottoTableMetadata.CONTENT_TYPE;
		  default: 
		      throw new IllegalArgumentException("Unknown URI " + uri); 
		  } 
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if ( uriMatcher.match(uri) != INCOMING_LOTTO_TABLE ) { 
            throw new IllegalArgumentException("Unknown URI " + uri); 
        } 
        Long now = Long.valueOf(System.currentTimeMillis()); 
 
        //validate input fields 
        // Make sure that the fields are all set 
        if (values.containsKey(LottoTableMetadata.CREATED_DATE) == false) { 
            values.put(LottoTableMetadata.CREATED_DATE, now); 
        } 
        
        if (values.containsKey(LottoTableMetadata.LOTTO_DATE) == false) { 
            throw new SQLException( 
               "Failed to insert row because Lotto Date is needed " + uri); 
        }
 
        SQLiteDatabase db = databaseHelper.getWritableDatabase(); 
        long rowId = db.insert(LottoTableMetadata.TABLE_NAME,LottoTableMetadata.LOTTO_DATE, values); 
        if (rowId > 0) { 
            Uri insertedBookUri = ContentUris.withAppendedId(LottoTableMetadata.CONTENT_URI, rowId); 
            getContext().getContentResolver().notifyChange(insertedBookUri, null); 
            return insertedBookUri; 
        } 
 
        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override 
	public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs, String sortOrder)  
	{ 
	        SQLiteQueryBuilder qb = new SQLiteQueryBuilder(); 
	        String limit = null;
	 
	        switch (uriMatcher.match(uri))  
	        { 
	            case INCOMING_LOTTO_MONTH_URI_INDICATOR: 
	            	qb.setTables(LottoTableMetadata.TABLE_NAME); 
	            	qb.setProjectionMap(lottoProjectionMap);
	            	// TODO: Month Format is MM-YYYY
	            	qb.appendWhere(LottoTableMetadata.LOTTO_YEARMONTH + "=" + uri.getPathSegments().get(1));
	            	break; 
	            case INCOMING_LOTTO_SINGLE_URI_INDICATOR: 
	            	qb.setTables(LottoTableMetadata.TABLE_NAME); 
	            	qb.setProjectionMap(lottoProjectionMap);
	            	// TODO: Day format is dd-MM-YYYY
	            	qb.appendWhere(LottoTableMetadata.LOTTO_DATE + "=" + uri.getPathSegments().get(1)); 
	            	break;
	            case INCOMING_LOTTO_TABLE:
	            	qb.setTables(LottoTableMetadata.TABLE_NAME); 
	            	qb.setProjectionMap(lottoProjectionMap);
	            	break;
	            default: 
	            	throw new IllegalArgumentException("Unknown URI " + uri); 
	        } 
	        // If no sort order is specified use the default 
	        String orderBy; 
	        if (TextUtils.isEmpty(sortOrder)) { 
	            orderBy = LottoTableMetadata.DEFAULT_SORT_ORDER; 
	        } else { 
	            orderBy = sortOrder; 
	        } 
	        // Get the database and run the query 
	        SQLiteDatabase db = databaseHelper.getReadableDatabase(); 
	        Cursor c = qb.query(db, projection, selection,  
	                               selectionArgs, null, null, orderBy, limit); 
	        //int i = c.getCount(); 
	 
	        // Tell the cursor what uri to watch,  
	        // so it knows when its source data changes 
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c; 
	    } 
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) 
	{
		//SQLiteDatabase db = databaseHelper.getWritableDatabase(); 
	    int count = 0; 
	    switch (uriMatcher.match(uri)) { 
	    case INCOMING_LOTTO_MONTH_URI_INDICATOR: 
	        // count = db.update(LottoTableMetadata.TABLE_NAME, values, where, whereArgs); 
	        break; 
	    case INCOMING_LOTTO_SINGLE_URI_INDICATOR: 
//	        String rowId = uri.getPathSegments().get(1); 
//	        count = db.update(LottoTableMetadata.TABLE_NAME, values, LottoTableMetadata._ID + "=" + rowId 
//	              + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs); 
	        break;
	    default: 
	        throw new IllegalArgumentException("Unknown URI " + uri); 
	    } 
	
	    getContext().getContentResolver().notifyChange(uri, null); 
	    return count; 
	} 


}

package com.ecyware.android.lottopanama.lottoprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class LottoProviderMetadata 
{

	public static final String AUTHORITY = "com.ecyware.android.lottopanama.lottoprovider.LottoProvider";
	public static final String DATABASE_NAME = "lotto.db";
	public static final int DATABASE_VERSION = 1;
	
	private LottoProviderMetadata()
	{
	}
	
	public static final class LottoTableMetadata implements BaseColumns
	{
		private LottoTableMetadata() 
		{
		}
		
		public static final String TABLE_NAME = "lottoNumbers";
		
		// uri and MIME type definitions
		public static final Uri CONTENT_URI
		= Uri.parse("content://" + AUTHORITY + "/lottoNumbers");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.lottonumber"; 
		public static final String CONTENT_MONTH_ITEM_TYPE = "vnd.android.cursor.month.item/vnd.lottonumber";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.lottonumber"; 

		public static final String DEFAULT_SORT_ORDER = "created DESC"; 

		//Additional Columns start here. 
		public static final String FIRST_PRIZE = "first"; 
		public static final String SECOND_PRIZE = "second"; 
		public static final String THIRD_PRIZE = "third"; 
		public static final String FOLIO = "folio"; 
		public static final String LETRAS = "letras"; 
		public static final String SERIE = "serie";
		public static final String LOTTO_TYPE = "lottoType";
		// string dd-MM-YYYY
		public static final String LOTTO_DATE = "lottoDate";
		// string MM-YYYY
		public static final String LOTTO_YEARMONTH = "lottoYearMonth";
		// date System.currentTimeMillis
		public static final String CREATED_DATE = "created"; 
		public static final String LOTTO_TYPED_DATE = "lottoTypedDate"; 		
		
	}
}
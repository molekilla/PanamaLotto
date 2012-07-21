package com.ecyware.android.lottopanama.logic;

import java.util.ArrayList;
import java.util.Date;
import android.database.Cursor;

import com.ecyware.android.lottopanama.lottoprovider.LottoProviderMetadata.LottoTableMetadata;


@SuppressWarnings("serial")
public class LottoItems extends ArrayList<LottoItem> {

	public LottoItems()
	{
	}
	
	private String getCursorString(Cursor cursor, String columnName) {
		return cursor.getString(cursor.getColumnIndex(columnName));
	}
	private Date readLottoDateFromMilliseconds(String value) {
		Date typedDate = new Date();
		Long milliseconds = Long.parseLong(value);
		typedDate.setTime(milliseconds);
		return typedDate;
	}
	
	public LottoItem addFromCursor(Cursor cursor) {
		LottoItem item = new LottoItem();
		item.setFirstNumber(getCursorString(cursor,LottoTableMetadata.FIRST_PRIZE));
		item.setSecondNumber(getCursorString(cursor,LottoTableMetadata.SECOND_PRIZE));
		item.setThirdNumber(getCursorString(cursor,LottoTableMetadata.THIRD_PRIZE));
		item.setFolio(getCursorString(cursor, LottoTableMetadata.FOLIO));
		item.setSerie(getCursorString(cursor, LottoTableMetadata.SERIE));
		item.setLetters(getCursorString(cursor, LottoTableMetadata.LETRAS));
		item.setHeader(getCursorString(cursor, LottoTableMetadata.LOTTO_TYPE));
		item.setLottoDate(getCursorString(cursor, LottoTableMetadata.LOTTO_DATE));

		String value = getCursorString(cursor, LottoTableMetadata.LOTTO_TYPED_DATE);
		Date lottoDate = this.readLottoDateFromMilliseconds(value);
		item.setLottoDateTyped(lottoDate);

		this.add(item);

		return item;
	}
}

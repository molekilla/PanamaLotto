package com.ecyware.android.lottopanama.logic;

import java.util.List;

public interface ILottoDataService 
{
    void refreshLottoData(int lastMonths);
	List<LottoItem> readLottoByDate(String id);
	List<LottoItem> readCurrentLotto();
	List<LottoItem> readLottoHistory();
    List<Integer> generatePyramidProjection();
    List<LottoItem> addLottoMonth(int year, int month);
}

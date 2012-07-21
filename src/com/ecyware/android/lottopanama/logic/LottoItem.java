package com.ecyware.android.lottopanama.logic;

import com.ecyware.android.lottopanama.CalendarHelper;

import android.content.Context;

import java.util.Date;

public class LottoItem {

	private String header;
	private Date lottoDateTyped;
	private String lottoDate;
	private String firstNumber;
	private String secondNumber;
	private String thirdNumber;
	private String letters;
	private String serie;
	private String folio;
	private String yearMonth;
	
	public LottoItem() {}

    public String getLottoHeader(Context context) {
        int month = CalendarHelper.getMonth(this.getLottoDateTyped());
        int monthResourceId = context.getResources().getIdentifier(
                context.getPackageName() + ":string/month_" + month, null, null);
        Integer yearText = this.getLottoDateTyped().getYear() + 1900;

        return this.getHeader().substring(0, 3) + "."
                + " - " + this.getLottoDateTyped().getDate()
                + " de " + context.getResources().getString(monthResourceId)
                + " de " + yearText;
    }

    public String getLottoDetail() {
        return "1. " + this.getFirstNumber() + " / " +
                "2. " + this.getSecondNumber() + " / " +
                "3. " + this.getThirdNumber() + " // panamalotto.com/mobile";
    }

	public void setHeader(String header) {
		this.header = header;
	}
	public String getHeader() {
		return this.header;
	}

	public void setLottoYearMonth(String lottoYearMonth) {
		this.yearMonth = lottoDate;
	}

	public String getLottoYearMonth() {
		return yearMonth;
	}
	
	public void setLottoDate(String lottoDate) {
		this.lottoDate = lottoDate;
	}

	public String getLottoDate() {
		return lottoDate;
	}

	public void setFirstNumber(String firstNumber) {
		this.firstNumber = firstNumber;
	}

	public String getFirstNumber() {
		return firstNumber;
	}

	public void setSecondNumber(String secondNumber) {
		this.secondNumber = secondNumber;
	}

	public String getSecondNumber() {
		return secondNumber;
	}

	public void setThirdNumber(String thirdNumber) {
		this.thirdNumber = thirdNumber;
	}

	public String getThirdNumber() {
		return thirdNumber;
	}

	public void setLottoDateTyped(Date lottoDateTyped) {
		this.lottoDateTyped = lottoDateTyped;
	}

	public Date getLottoDateTyped() {
		return lottoDateTyped;
	}

	public void setLetters(String letters) {
		this.letters = letters;
	}

	public String getLetters() {
		return letters;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getSerie() {
		return serie;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getFolio() {
		return folio;
	}
	
}

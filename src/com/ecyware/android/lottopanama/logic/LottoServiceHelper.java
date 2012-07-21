package com.ecyware.android.lottopanama.logic;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LottoServiceHelper {

	public LottoServiceHelper() {

	}

	private static String request(HttpResponse response){
        String result = "";
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
            result = "Error";
        }
        return result;
    }

	public static String hash(String data)
	{
		return md5(data);
	}

    public static LottoHtmlContext getLottoHtml(String url) {
		HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try{
            HttpResponse response = client.execute(request);
            String html = request(response);
            LottoHtmlContext ctx = new LottoHtmlContext(html, hash(html));
            return ctx;
        }catch(Exception ex){
            return new LottoHtmlContext("", "");
        }
	}
	
	private static String md5(String in) {
	    MessageDigest digest;
	    try {
	        digest = MessageDigest.getInstance("MD5");
	        digest.reset();
	        digest.update(in.getBytes());
	        byte[] a = digest.digest();
	        int len = a.length;
	        StringBuilder sb = new StringBuilder(len << 1);
	        for (int i = 0; i < len; i++) {
	            sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
	            sb.append(Character.forDigit(a[i] & 0x0f, 16));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	    return null;
	}
	
	public static List<LottoItem> readLottoWebDataByMonth(String url,
			String dateId) {
		String parsedUrl = String.format(url, dateId.substring(7, 10),
				dateId.substring(4, 5));
		return readLottoWebData(parsedUrl, null);
	}

	// read lotto data from web
	public static List<LottoItem> readLottoWebData(String url,String data) {
		try {
			LottoItems lottoItems = new LottoItems();
			Document doc;
			
			if ( url == null )
			{
				doc = Jsoup.parse(data);
			} else {
				doc = Jsoup.connect(url).get();
			}
			
			Elements rows = doc.select("table[border=1]").first()
					.select("tr[bgcolor]");
			for (int i = 0; i < rows.size(); i++) {
				Elements fields = rows.get(i).select("td");
				LottoItem item = new LottoItem();
				item.setHeader(fields.get(0).text());
				item.setLottoDate(fields.get(1).text());
				item.setFirstNumber(fields.get(2).text());
				item.setLetters(fields.get(3).text());
				item.setSerie(fields.get(4).text());
				item.setFolio(fields.get(5).text());
				item.setSecondNumber(fields.get(7).text());
				item.setThirdNumber(fields.get(10).text());

				try {
					DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
					Date date = df.parse(item.getLottoDate());
					item.setLottoDateTyped(date);
					Calendar c = Calendar.getInstance();
					c.setTime(date);
					item.setLottoYearMonth(c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR));
				} catch (ParseException ex) {
					// TODO: nothing here...
				}
				lottoItems.add(item);

			}
			return lottoItems;
        } catch (Exception e) {
            Log.e("readLottoWebData", e.toString());
			return new LottoItems();
		}

	}
}

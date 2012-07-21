package com.ecyware.android.lottopanama;

import com.ecyware.android.lottopanama.logic.ILottoDataService;
import com.ecyware.android.lottopanama.logic.LottoItem;
import com.ecyware.android.lottopanama.logic.LottoService;
import com.ecyware.android.lottopanama.logic.LottoService.LottoServiceBinder;
import com.ecyware.android.lottopanama.lottoprovider.LottoProviderMetadata.LottoTableMetadata;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.widget.TextView;

import java.util.List;

public class LottoDetail  extends RoboActivity {
	@InjectView(R.id.lottoNumberOne) private TextView firstPrize;
	@InjectView(R.id.lottoNumberTwo) private TextView secondPrize;
	@InjectView(R.id.lottoNumberThree) private TextView thirdPrize;
	@InjectView(R.id.lottoLetters) private TextView letters;
	@InjectView(R.id.lottoSerie) private TextView serie;
	@InjectView(R.id.lottoFolio) private TextView folio;
	@InjectResource(R.string.es_date_separator) private String esDateSeparator;
	@InjectResource(R.string.loading_data_text) private String loadingDataText;
	
	String lottoDateValue = "";
    ILottoDataService lottoService;
    boolean isLottoServiceBound = false;
    ProgressDialog dialog = null;
    String empty = "";
    boolean doReadCurrentLotto = false;
    TextView header;
    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lotto_detail);
        header = (TextView) this.findViewById(R.id.lottoDetailHeader).findViewById(
                R.id.lottoActionBarText);
        header.setText(empty);
        firstPrize.setText(empty);
        secondPrize.setText(empty);
        thirdPrize.setText(empty);
        serie.setText(empty);
        letters.setText(empty);  
        folio.setText(empty);
        
        Bundle intentBundle = getIntent().getExtras();
        if ( intentBundle != null )
        {
        	doReadCurrentLotto = false;
        	setTitle("Sorteo seleccionado");
        	// read selected lotto by date
        	lottoDateValue = intentBundle.getString(LottoTableMetadata.LOTTO_DATE);
        } else {
        	doReadCurrentLotto = true;
        	setTitle("Ultimo sorteo");
        }
        
        // Show progress bar
        dialog = ProgressDialog.show(this, "", loadingDataText, true);
    }

    //////////////////
    // Service calls 
    //////////////////
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, LottoService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (isLottoServiceBound) {
            unbindService(serviceConnection);
            isLottoServiceBound = false;
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	LottoServiceBinder binder = (LottoServiceBinder) service;
        	lottoService = binder.getService();
            isLottoServiceBound = true;
            
            new LottoAsyncTask().execute(doReadCurrentLotto, lottoDateValue);
            
        }

        @Override
        public void onServiceDisconnected(ComponentName arg) {
        	isLottoServiceBound = false;
        }
    };
    ///////////////////
    // Async Task calls
    ///////////////////
	// Loads the numbers async
    private class LottoAsyncTask extends AsyncTask<Object, Void, List<LottoItem>> {
		protected List<LottoItem> doInBackground(Object... options)
		{				
			boolean isReadCurrentLotto = (Boolean)options[0];
			String lottoDateID = (String)options[1];
			if ( isReadCurrentLotto )
			{
				return lottoService.readCurrentLotto();	
			}
			else {
				return lottoService.readLottoByDate(lottoDateID);
			}
		}
		protected void onPostExecute(List<LottoItem> result)
		{
			if ( result.size() > 0 )
			{
				updateView(result);
				dialog.dismiss();
			}
		}
	   // assign async callback
	   private void updateView(List<LottoItem> result)
	   {      
		    LottoItem lastItem = result.get(result.size()-1);
		    int month = CalendarHelper.getMonth(lastItem.getLottoDateTyped());
		    int monthResourceId = getResources().getIdentifier(getPackageName() + ":string/month_" + month  ,null,null);
	        Integer yearText = lastItem.getLottoDateTyped().getYear() + 1900;
	        

	        header.setText(lastItem.getHeader() 
	        		+ " - " + lastItem.getLottoDateTyped().getDate()
	        		+ " " + esDateSeparator + " " +  getResources().getString(monthResourceId)
	        		+ " " +  esDateSeparator + " " + yearText );
	        firstPrize.setText(lastItem.getFirstNumber());
	        secondPrize.setText(lastItem.getSecondNumber());
	        thirdPrize.setText(lastItem.getThirdNumber());
	        serie.setText(lastItem.getSerie());
	        letters.setText(lastItem.getLetters());  
	        folio.setText(lastItem.getFolio());
	   }
    }
}

package com.ecyware.android.lottopanama;

import com.ecyware.android.lottopanama.logic.ILottoDataService;
import com.ecyware.android.lottopanama.logic.LottoItem;
import com.ecyware.android.lottopanama.logic.LottoService;
import com.ecyware.android.lottopanama.logic.LottoService.LottoServiceBinder;
import com.ecyware.android.lottopanama.lottoprovider.LottoProviderMetadata.LottoTableMetadata;
import com.ecyware.android.lottopanama.ui.LottoAdapter;
import com.ecyware.android.lottopanama.ui.LottoExpandableListAdapter;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LottoHistory extends ExpandableListActivity {

	LottoAdapter lottoAdapter;
    ExpandableListAdapter expandedlistAdapter;

	private ArrayList<LottoItem> lottoItems = null;
	ILottoDataService lottoService;
    boolean isLottoServiceBound = false;
    ProgressDialog dialog = null;
    boolean canRequestData = true;
    

    private String loadingDataText;
	
	public LottoHistory()
	{
		
	}
	
    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lotto_historical);
        loadingDataText = this.getResources().getString(R.string.loading_data_text);
		this.lottoItems = new ArrayList<LottoItem>();
        LottoItem lotto = new LottoItem();
        lotto.setFirstNumber("");
        lotto.setSecondNumber("");
        lotto.setThirdNumber("");
        this.lottoItems.add(lotto);


        Map<String, Integer> groups = getGroups();
        ArrayList<ArrayList<LottoItem>> children = new ArrayList<ArrayList<LottoItem>>();
        children.add(lottoItems);

        this.expandedlistAdapter = new LottoExpandableListAdapter(this, groups, children);
        this.setListAdapter(expandedlistAdapter);
        this.getExpandableListView().setGroupIndicator(null);
        // this.getExpandableListView().setTextFilterEnabled(true);

		// Show progress bar
        dialog = ProgressDialog.show(this, "", loadingDataText, true);
	}
	
    private Map<String, Integer> getGroups()
    {
        Map<String, Integer> groups = new HashMap<String, Integer>();
        groups.put("82011", 0);
        return groups;
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //Bundle extras = intent.getExtras();
        switch(requestCode) 
        {
        	case Constants.LOTTO_VIEW_ITEM:
        		canRequestData = false;
                break;
        }
    }
	
    @Override
    public boolean onChildClick(ExpandableListView listView, View view, int groupPosition,
            int position, long id) {
        boolean result = super.onChildClick(listView, view, groupPosition, position, id);

        // get current item
        LottoItem lotto = (LottoItem) expandedlistAdapter.getChild(groupPosition, position);
        String lottoDateID = lotto.getLottoDate();
        Intent intentSelectedLotto = new Intent(this, LottoDetail.class);
        intentSelectedLotto.putExtra(LottoTableMetadata.LOTTO_DATE, lottoDateID);
        this.startActivityForResult(intentSelectedLotto,
                Constants.LOTTO_VIEW_ITEM);

        return result;
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
            
            if ( canRequestData )
            {
            	new LottoAsyncTask().execute();
            }
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
    private class LottoAsyncTask extends AsyncTask<String, Void, List<LottoItem>> {
		protected List<LottoItem> doInBackground(String... url)
		{				
			return lottoService.readLottoHistory();								
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
            ((LottoExpandableListAdapter) expandedlistAdapter).updateChildren(result);
            getExpandableListView().expandGroup(0);
	   }
    }
}

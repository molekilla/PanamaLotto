package com.ecyware.android.lottopanama.ui;

import com.ecyware.android.lottopanama.CalendarHelper;
import com.ecyware.android.lottopanama.R;
import com.ecyware.android.lottopanama.logic.LottoItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LottoAdapter extends ArrayAdapter<LottoItem> {

    private ArrayList<LottoItem> items;

    public LottoAdapter(Context context, int textViewResourceId, ArrayList<LottoItem> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lotto_listview_item, null);
        }
        LottoItem lottoItem = items.get(position);
        if (lottoItem != null) {
            int month = CalendarHelper.getMonth(lottoItem.getLottoDateTyped());
            String resourceId = this.getContext().getPackageName() + ":string/month_" + month;
            int monthResourceId = this
                    .getContext()
                    .getResources()
                    .getIdentifier(resourceId, null, null);
            // Bind
            // TextView monthLabel = (TextView) v.findViewById(R.id.monthText);
            TextView dayLabel = (TextView) v.findViewById(R.id.dayText);
            // TextView yearLabel = (TextView) v.findViewById(R.id.yearText);
            TextView header = (TextView) v.findViewById(R.id.lottoViewHeader);
            TextView lottoText = (TextView) v.findViewById(R.id.lottoViewText);

            header.setText(lottoItem.getHeader());
            // monthLabel.setText(monthValue.substring(0, 3));
            dayLabel.setText(String.valueOf(lottoItem.getLottoDateTyped().getDate()));
            // yearLabel.setText(String.valueOf(yearValue));

            lottoText.setText(String.format("1. %s \r\n2. %s \r\n3. %s",
                    lottoItem.getFirstNumber(),
                    lottoItem.getSecondNumber(),
                    lottoItem.getThirdNumber()));
        }
        return v;
    }
}

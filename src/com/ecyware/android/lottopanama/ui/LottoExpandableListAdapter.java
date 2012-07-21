package com.ecyware.android.lottopanama.ui;

import com.ecyware.android.lottopanama.CalendarHelper;
import com.ecyware.android.lottopanama.R;
import com.ecyware.android.lottopanama.logic.LottoItem;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LottoExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, Integer> groups;
    private ArrayList<ArrayList<LottoItem>> children;

    public LottoExpandableListAdapter(Context context, Map<String, Integer> groups,
            ArrayList<ArrayList<LottoItem>> children) {
        this.context = context;
        this.groups = groups;
        this.children = children;
    }

    public void updateChildren(List<LottoItem> lottoItems)
    {
        // int month = CalendarHelper.currentMonth();
        // int year = CalendarHelper.currentYear();

        Map<String, ArrayList<LottoItem>> lottoCalendar = new HashMap<String, ArrayList<LottoItem>>();
        List<String> keys = new ArrayList<String>();
        for (LottoItem item : lottoItems)
        {
            String key = String.valueOf(CalendarHelper.getMonth(item.getLottoDateTyped()))
                    + String.valueOf(CalendarHelper.getYear(item.getLottoDateTyped()));
            if (!lottoCalendar.containsKey(key))
            {
                lottoCalendar.put(key, new ArrayList<LottoItem>());
                keys.add(key);
            }

            // add
            lottoCalendar.get(key).add(item);
        }
        this.notifyDataSetChanged();
        this.groups = new HashMap<String, Integer>();
        this.children.clear();
        int index = 0;
        for (String key : keys) {
            // add group
            this.groups.put(key, index);

            // add children
            children.add(lottoCalendar.get(key));
            index++;

        }
        this.notifyDataSetChanged();


    }
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (children.size() == 0)
        {
            return null;
        }
        else
        {
            return children.get(groupPosition).get(childPosition);
        }
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(this.context);
        textView.setLayoutParams(lp);

        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        // Set the text starting position
        textView.setPadding(36, 0, 0, 0);
        return textView;
    }

    // Return a child view. You can load your custom layout here.
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
            View convertView, ViewGroup parent) {
        LottoItem lottoItem = (LottoItem) getChild(groupPosition, childPosition);
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lotto_listview_item, null);
        }
        // LottoItem lottoItem = items.get(position);
        if (lottoItem != null) {

            // Bind
            TextView dayLabel = (TextView) v.findViewById(R.id.dayText);
            TextView header = (TextView) v.findViewById(R.id.lottoViewHeader);
            TextView lottoText = (TextView) v.findViewById(R.id.lottoViewText);
            TextView weekdayLabel = (TextView) v.findViewById(R.id.weekdayText);

            header.setText(lottoItem.getHeader());
            String formattedDay = String.valueOf(lottoItem.getLottoDateTyped().getDate());
            if (formattedDay.length() == 1)
            {
                formattedDay = "0" + formattedDay;
            }
            String formattedWeekday = CalendarHelper.getDayOfWeekFromResource(
                    lottoItem.getLottoDateTyped(), context).substring(0, 3);

            weekdayLabel.setText(formattedWeekday);
            dayLabel.setText(formattedDay);
            lottoText.setText(String.format("1. %s / 2. %s / 3. %s",
                    lottoItem.getFirstNumber(),
                    lottoItem.getSecondNumber(),
                    lottoItem.getThirdNumber()));
        }
        return v;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // Return a group view. You can load your custom layout here.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
            ViewGroup parent) {
        LottoItem lottoItem = (LottoItem) getChild(groupPosition, 0);
        if (lottoItem == null)
            return convertView;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.lotto_listview_header, null);
        }

        TextView headerView = (TextView) convertView.findViewById(R.id.lottoHistoryGroupHeaderText);
        headerView.setText(context.getResources().getString(R.string.loading_data_text));
        if (lottoItem.getFirstNumber() != "")
        {
            String monthValue = CalendarHelper.getMonthFromResource(lottoItem.getLottoDateTyped(),
                    context);
            Integer yearValue = lottoItem.getLottoDateTyped().getYear() + 1900;
            String label = monthValue + " " + String.valueOf(yearValue);

            headerView.setText(label);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public Object getGroup(int position) {
        return groups.get(position);
    }

}
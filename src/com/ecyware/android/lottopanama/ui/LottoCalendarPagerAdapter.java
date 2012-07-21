package com.ecyware.android.lottopanama.ui;

import com.ecyware.android.lottopanama.R;
import com.ecyware.android.lottopanama.logic.LottoItem;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class LottoCalendarPagerAdapter  extends PagerAdapter {

    private static int MAX_CALENDAR_COUNT = 12;
    protected final Activity currentActivity;
    
    public LottoCalendarPagerAdapter(Activity activity)
    {
        currentActivity = activity;

    }
    @Override
    public int getCount() {
            return MAX_CALENDAR_COUNT;
    }

    /**
     * Create the page for the given position.  The adapter is responsible
     * for adding the view to the container given here, although it only
     * must ensure this is done by the time it returns from
     * {@link #finishUpdate()}.
     *
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page.  This does not
     * need to be a View, but can be some other container of the page.
     */
    @Override
    public Object instantiateItem(View container, int position) {
        ViewPager viewPager = (ViewPager) container;
        // LayoutInflater inflater = currentActivity.getLayoutInflater();

        // View view = (View)
        // inflater.inflate(R.layout.lotto_listview_fragment,null);
        // LinearLayout listView = (LinearLayout)view;
        ListView listView = new ListView(viewPager.getContext());
        ArrayList<LottoItem> lottoItems = new ArrayList<LottoItem>();
        LottoAdapter lottoAdapter = new LottoAdapter(viewPager.getContext(),
                R.layout.lotto_listview_item, lottoItems);
        listView.setAdapter(lottoAdapter);
        viewPager.addView(listView, 0);

        return listView;
    }

/**
 * Remove a page for the given position.  The adapter is responsible
 * for removing the view from its container, although it only must ensure
 * this is done by the time it returns from {@link #finishUpdate()}.
 *
 * @param container The containing View from which the page will be removed.
 * @param position The page position to be removed.
 * @param object The same object that was returned by
 * {@link #instantiateItem(View, int)}.
 */
    @Override
    public void destroyItem(View container, int position, Object view) {
        ViewPager viewPager = (ViewPager) container;
        viewPager.removeView((View) view);
    }

    
    
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    
/**
 * Called when the a change in the shown pages has been completed.  At this
 * point you must ensure that all of the pages have actually been added or
 * removed from the container as appropriate.
 * @param container The containing View which is displaying this adapter's
 * page views.
 */
    @Override
    public void finishUpdate(View arg0) {}
    

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {}

    @Override
    public Parcelable saveState() {
            return null;
    }

    @Override
    public void startUpdate(View arg0) {}

}


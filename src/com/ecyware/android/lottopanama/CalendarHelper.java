package com.ecyware.android.lottopanama;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class CalendarHelper {

    public static Calendar getNotificationCalendarNextDate(int hour, int minute)
    {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.DATE, 1);
        currentCalendar.set(Calendar.HOUR_OF_DAY, hour);
        currentCalendar.set(Calendar.MINUTE, minute);
        return currentCalendar;

    }

    public static Calendar getNotificationCalendarDate(int hour, int minute)
    {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.HOUR_OF_DAY, hour);
        currentCalendar.set(Calendar.MINUTE, minute);
        return currentCalendar;

    }
    public static String getMonthFromResource(Date date, Context context)
    {
        String resourceName = context.getPackageName() + ":string/month_"
                + CalendarHelper.getMonth(date);
        int resourceId = context
                .getResources()
                .getIdentifier(resourceName, null, null);

        return context.getResources().getString(resourceId);
    }

    public static String getDayOfWeekFromResource(Date date, Context context)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        String resourceName = context.getPackageName() + ":string/weekday_" + dayOfWeek;
        int resourceId = context
                .getResources()
                .getIdentifier(resourceName, null, null);

        return context.getResources().getString(resourceId);
    }

	public static int getMonth(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH) + 1;
	}
	public static int getYear(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	public static int getDay(Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DATE);
	}

    public static int currentMonth()
    {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        return month;
    }

    public static int currentYear()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year;
    }

    public static int currentDay()
    {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        return day;
    }
}

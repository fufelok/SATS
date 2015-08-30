package se.leanbit.sats.repositories.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.repositories.interfaces.SatsTimeFormatInterface;

public class SatsTimeFormatService implements SatsTimeFormatInterface
{
    private final String months[] = {"Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Augusti", "September", "Oktober", "November", "Decemober"};
    private final String weekDays[] = {"", "Söndag", "Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag"};
    private final static int YEAR = 2015;
    @Override
    public String getDate(final SatsActivity activity)
    {
        final Calendar activityDate = getDateCalendar(activity.date);
        final int currentDate = activityDate.get(Calendar.DATE);
        final String currentMonth = months[activityDate.get(Calendar.MONTH)];

        return "" + currentDate + " " + currentMonth;
    }

    @Override
    public String getDayName(final SatsActivity activity)
    {
        final Calendar activityDate = getDateCalendar(activity.date);
        return weekDays[activityDate.get(activityDate.DAY_OF_WEEK)];
    }

    @Override
    public String[] getHoursMinutes(final SatsActivity activity)
    {
        String split[] = activity.date.split(" ");
        String timeSplit[] = split[1].split(":");

        String returnStr[] = new String[2];
        returnStr[0] = timeSplit[0];
        returnStr[1] = timeSplit[1];
        return returnStr;
    }

    @Override
    public String getWeekDates(final SatsActivity activity)
    {
        //Vecka 14 (30/3-5/4)
        Calendar activityDate = getDateCalendar(activity.date);
        activityDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        StringBuilder builder = new StringBuilder();
        builder.append(" " + activityDate.get(Calendar.DAY_OF_MONTH) + " -");
        activityDate.roll(Calendar.DAY_OF_YEAR, 6);
        builder.append(" " + activityDate.get(Calendar.DAY_OF_MONTH) + "/" + (activityDate.get(Calendar.MONTH) + 1));

        return builder.toString();
    }

    @Override
    public int getWeekNum(final SatsActivity activity)
    {
        final Calendar activityDate = getDateCalendar(activity.date);

        return activityDate.get(activityDate.WEEK_OF_YEAR);
    }

    public Boolean isToday(final SatsActivity activity)
    {
        Calendar calendar = getDateCalendar(activity.date);
        Calendar currentCalendar = Calendar.getInstance();
        Date date = new Date();
        currentCalendar.setTime(date);

        final boolean sameDay = calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
            && calendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR);

        return sameDay;
    }

    private Calendar getDateCalendar(final String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date activityDate = new Date();
        try
        {
            activityDate = dateFormat.parse(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activityDate);

        return calendar;
    }
    public Boolean weekIsPast(int weekNum)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, weekNum);
        calendar.set(Calendar.YEAR, YEAR);

        Date date = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(date);
        if (currentCal.after(calendar) && weekNum != currentCal.get(Calendar.WEEK_OF_YEAR))
        {
            return true;
        }

        return false;
    }

    public Boolean isCurrentWeek(int weekNum)
    {

        Date date = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(date);
        int currentWeekNum = currentCal.get(Calendar.WEEK_OF_YEAR);

        if (weekNum == currentWeekNum)
        {
            return true;
        }

        return false;
    }

    public String getStartEndOFWeekByWeekNum(int enterWeek)
    {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, YEAR);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        StringBuilder builder = new StringBuilder();
        builder.append(" " + calendar.get(Calendar.DAY_OF_MONTH) + " -");
        calendar.roll(Calendar.DAY_OF_YEAR, 6);
        builder.append(" " + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1));

        return builder.toString();
    }
    public int getCurrentWeekNum()
    {
        Date date = new Date();
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(date);

        return currentCal.get(Calendar.WEEK_OF_YEAR);
    }
}

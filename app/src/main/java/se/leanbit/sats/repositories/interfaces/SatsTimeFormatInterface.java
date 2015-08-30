package se.leanbit.sats.repositories.interfaces;

import se.leanbit.sats.models.SatsActivity;

public interface SatsTimeFormatInterface
{
    //Example: 3 April
    public String getDate(final SatsActivity activity);
    //Example: fredag
    public String getDayName(final SatsActivity activity);
    //Return an array with hours and minutes as separate strings
    public String[] getHoursMinutes(final SatsActivity activity);
    //Example: 25 - 31/1
    public String getWeekDates(final SatsActivity activity);
    //Example 1
    public int getWeekNum(final SatsActivity activity);
}

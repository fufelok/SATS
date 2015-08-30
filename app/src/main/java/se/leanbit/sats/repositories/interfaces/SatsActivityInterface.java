package se.leanbit.sats.repositories.interfaces;


import java.util.LinkedHashMap;

import se.leanbit.sats.models.SatsActivity;

public interface SatsActivityInterface
{
    SatsActivity[] getActivitiesBetween(final String fromDate, final String toDate);
    String getActivityName(final SatsActivity activity);
    String getGroupType(final SatsActivity activity);
    String getRegion(final SatsActivity activity);
    Boolean isCustom(final SatsActivity activity);
    int que(final SatsActivity activity);
    int duration(final SatsActivity activity);
    Boolean isCompleted(final SatsActivity activity);
    String instructor(final SatsActivity activity);
    Boolean comments(final SatsActivity activity);
    Boolean isPast(final SatsActivity activity);

    //Returns an integer the represents the number of passes we had on our most busy week.
    int getMaxTraning(final SatsActivity activity[]);
    //Returns a week based map.
    //Example KEY: int (week)
    //Example VALUE: int (number of activities this week)
    LinkedHashMap<Integer, Integer> getTraningMap(final SatsActivity activity[]);
}

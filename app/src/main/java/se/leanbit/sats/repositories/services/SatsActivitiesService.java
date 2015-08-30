package se.leanbit.sats.repositories.services;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import se.leanbit.sats.models.SatsActivities;
import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.models.SatsCenters;
import se.leanbit.sats.models.SatsFullCenterModel;
import se.leanbit.sats.models.SatsFullCenterRegion;
import se.leanbit.sats.models.SatsSimpleCenter;
import se.leanbit.sats.repositories.interfaces.SatsActivityInterface;

public class SatsActivitiesService implements SatsActivityInterface
{
    private static HashMap<String, String> centerMap = new HashMap<>();
    private static HashMap<String, SatsSimpleCenter> fullCenterMap = new HashMap<>();
    private String fromDate;
    private String toDate;


    @Override
    public SatsActivity[] getActivitiesBetween(final String fromDate, final String toDate)
    {

        this.fromDate = fromDate;
        this.toDate = toDate;
        WebService webService = new WebService();
        final String url = "http://leanbit.erikwelander.se/api.sats.com/v1.0/se/training/activities";
        String jsonResponse = "";
        try
        {
            jsonResponse = webService.execute(url + "/" + fromDate + "/" + toDate).get();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        activitiesParser activitiesParser = new activitiesParser();
        SatsActivities satsActivities = new SatsActivities();
        try
        {

            satsActivities = activitiesParser.execute(jsonResponse).get();

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        for (SatsActivity act : satsActivities.activities)
        {
            getRegion(act);
        }

        return satsActivities.activities;
    }

    @Override
    public String getActivityName(final SatsActivity activity)
    {
        return activity.subType;
    }

    @Override
    public String getGroupType(final SatsActivity activity)
    {
        return activity.type;
    }


    @Override
    public String getRegion(final SatsActivity activity)
    {
        final SatsActivity.SatsBooking booking = activity.booking;
        if (null != activity.booking)
        {
            if (centerMap.containsKey(activity.booking.centerId))
            {
                return centerMap.get(activity.booking.centerId);
            }
            WebService webService = new WebService();
            final String url = "https://api2.sats.com/v1.0/se/centers/";

            String jsonResponse = "";
            try
            {
                jsonResponse = webService.execute(url + activity.booking.centerId).get();
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            centerParser centerParser = new centerParser();
            try
            {

                SatsCenters satsCenters = centerParser.execute(jsonResponse).get();
                if (null != satsCenters)
                {
                    centerMap.put(activity.booking.centerId, satsCenters.center.name);
                    SatsSimpleCenter satsSimpleCenter = new SatsSimpleCenter(satsCenters.center.name, satsCenters.center.url,
                            satsCenters.center.lat, satsCenters.center.lon);
                    fullCenterMap.put(activity.booking.centerId, satsSimpleCenter);
                    return satsCenters.center.name;
                }

                return "";
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (ExecutionException e)
            {
                e.printStackTrace();
            }
        }

        return "";
    }

    @Override
    public Boolean isCustom(final SatsActivity activity)
    {
        return null == activity.booking;
    }

    @Override
    public int que(final SatsActivity activity)
    {
        return activity.booking.positionInQueue;
    }

    @Override
    public int duration(final SatsActivity activity)
    {
        return activity.durationInMinutes;
    }

    @Override
    public Boolean isCompleted(final SatsActivity activity)
    {
        return activity.status.equalsIgnoreCase("COMPLETED");
    }

    @Override
    public String instructor(final SatsActivity activity)
    {
        return activity.booking.clazz.instructorId;
    }

    @Override
    public Boolean comments(final SatsActivity activity)
    {
        return activity.comment.length() > 0;
    }

    @Override
    public Boolean isPast(final SatsActivity activity)
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date activityDate = new Date();

        try
        {
            activityDate = dateFormat.parse(activity.date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return (new Date().after(activityDate));
    }

    public LinkedHashMap<Integer, Integer> getTraningMap(final SatsActivity activity[])
    {
        SatsTimeFormatService satsTimeFormatService = new SatsTimeFormatService();
        LinkedHashMap<Integer, Integer> traningMap = new LinkedHashMap<>();

        for (int i = 0; i < activity.length; i++)
        {
            final int currentWeek = satsTimeFormatService.getWeekNum(activity[i]);
            if (traningMap.containsKey(currentWeek))
            {
                traningMap.put(currentWeek, traningMap.get(currentWeek) + 1);
            } else
            {
                traningMap.put(currentWeek, 1);
            }
        }

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        Date startDate = new Date();
        Date endDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            startDate = simpleDateFormat.parse(fromDate);
            startCal.setTime(startDate);

            endDate = simpleDateFormat.parse(toDate);
            endCal.setTime(endDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        int startWeek = startCal.get(Calendar.WEEK_OF_YEAR);
        int endWeek = endCal.get(Calendar.WEEK_OF_YEAR);
        int totWeeks = endWeek - startWeek;
        LinkedHashMap<Integer, Integer> completeTraningMap = new LinkedHashMap<>();

        for (int i = startWeek; i < endWeek + 1; i++)
        {
            if (traningMap.containsKey(i))
            {
                completeTraningMap.put(i, traningMap.get(i));

            } else
            {
                completeTraningMap.put(i, 0);
            }
        }

        return completeTraningMap;
    }

    public int getMaxTraning(final SatsActivity activity[])
    {
        LinkedHashMap<Integer, Integer> traningMap = getTraningMap(activity);
        int topTraningCount = 0;
        for (Integer value : traningMap.values())
        {
            if (topTraningCount < value)
            {
                topTraningCount = value;
            }
        }
        return topTraningCount;
    }

    private class activitiesParser extends AsyncTask<String, String, SatsActivities>
    {

        @Override
        protected SatsActivities doInBackground(String... params)
        {
            Gson gson = new GsonBuilder().create();
            SatsActivities satsActivities = gson.fromJson(params[0], SatsActivities.class);
            return satsActivities;
        }
    }

    private class centerParser extends AsyncTask<String, String, SatsCenters>
    {

        @Override
        protected SatsCenters doInBackground(String... params)
        {
            Gson gson = new GsonBuilder().create();
            SatsCenters satsCenters = gson.fromJson(params[0], SatsCenters.class);
            return satsCenters;
        }
    }

    public int getTotalTranings(LinkedHashMap<Integer, Integer> traningMap)
    {
        int totTranings = 0;
        for (int key : traningMap.keySet())
        {
            totTranings += traningMap.get(key);
        }

        return totTranings;
    }

    public void setFullCenterMap()
    {

        WebService webService = new WebService();
        final String url = "https://api2.sats.com/v1.0/se/centers";

        String jsonResponse = "";
        try
        {
            jsonResponse = webService.execute(url).get();
        } catch (Exception e)

        {
            e.printStackTrace();
        }


        Gson gson = new GsonBuilder().create();
        SatsFullCenterModel satsCenters = gson.fromJson(jsonResponse, SatsFullCenterModel.class);

        for (int i = 0; i < satsCenters.regions.length; i++)
        {

            for (int j = 0; j < satsCenters.regions[i].centers.length; j++)
            {
                String centerName = satsCenters.regions[i].centers[j].name;
                String centerUrl = satsCenters.regions[i].centers[j].url;
                double centerLat = satsCenters.regions[i].centers[j].lat;
                double centerLong = satsCenters.regions[i].centers[j].lon;
                SatsSimpleCenter satsSimpleCenter = new SatsSimpleCenter(centerName, centerUrl, centerLat, centerLong);
                fullCenterMap.put(centerName, satsSimpleCenter);
            }
        }
    }


    public HashMap<String, SatsSimpleCenter> getFullCenterMap()
    {
        return fullCenterMap;
    }

}


package se.leanbit.sats.models;

import java.io.Serializable;

public class SatsActivity implements Serializable
{
    public SatsBooking booking;
    public String comment;
    public String date;
    public int durationInKm;
    public int durationInMinutes;
    public String id;
    public String source;
    public String status;
    public String subType;
    public String type;

    public class SatsBooking implements Serializable
    {
        public String centerId;
        public SatsBookingClass clazz;
        public String id;
        public int positionInQueue;
        public String status;

        public class SatsBookingClass implements Serializable
        {
            public int bookedPersonsCount;
            public String[] classCategoryIds;
            public String classTypeId;
            public int durationInMinutes;
            public String id;
            public String instructorId;
            public int maxPersonsCount;
            public String name;
            public String regionId;
            public String startTime;
            public int waitingListCount;
        }
    }
}

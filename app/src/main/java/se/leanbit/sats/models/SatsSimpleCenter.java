package se.leanbit.sats.models;

public class SatsSimpleCenter
{
    public String centerName;
    public String webUrl;
    public double lat;
    public double Long;

    public SatsSimpleCenter(String centerName, String webUrl, double lat, double Long)
    {
        this.centerName = centerName;
        this.webUrl = webUrl;
        this.lat = lat;
        this.Long = Long;
    }
}

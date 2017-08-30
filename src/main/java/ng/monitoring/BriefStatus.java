package ng.monitoring;

/**
 * Created by adam on 8/29/17.
 */
public class BriefStatus
{
    private long lDateAsEpoch;
    private boolean bSiteIsUp;

    public long getlDateAsEpoch() {
        return lDateAsEpoch;
    }

    public boolean isbSiteUp() {
        return bSiteIsUp;
    }


    public String toString()
    {
        String sStr = String.valueOf(lDateAsEpoch) + " " + String.valueOf(this.bSiteIsUp);
        return sStr;
    }

    public BriefStatus(long alDateAsEpoch, boolean abSiteIsUp)
    {
        this.lDateAsEpoch = alDateAsEpoch;
        this.bSiteIsUp = abSiteIsUp;
    }


}

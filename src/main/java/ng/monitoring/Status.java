package ng.monitoring;

/**
 * Created by adam on 8/26/17.
 */
public class Status
{
    private long    lDateAsEpoch;
    private boolean bSiteIsUp;
    private String  errorMessage;
    private String  errorStep;

    public Status()
    {

    }

    public Status(long alDateAsEpoch, boolean abSiteIsUp)
    {
        this.lDateAsEpoch = alDateAsEpoch;
        this.bSiteIsUp = abSiteIsUp;
    }

    public Status(long alDateAsEpoch, boolean abSiteIsUp, String aErrorMessage, String aErrorStep)
    {
        this.lDateAsEpoch = alDateAsEpoch;
        this.bSiteIsUp = abSiteIsUp;
        this.errorMessage = aErrorMessage;
        this.errorStep = aErrorStep;
    }


    public long getEntryDateAsEpoch() {
        return lDateAsEpoch;
    }

    public void setEntryDateAsEpoch(long alDateAsEpoch) {
        this.lDateAsEpoch = alDateAsEpoch;
    }


    public boolean isSiteUp() {
        return bSiteIsUp;
    }

    public void setSiteIsUp(boolean abSiteIsUp) {
        this.bSiteIsUp = abSiteIsUp;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorStep() {
        return errorStep;
    }

    public void setErrorStep(String errorStep) {
        this.errorStep = errorStep;
    }


    public String toString()
    {
        String sStr = String.valueOf(lDateAsEpoch) + " " + String.valueOf(this.bSiteIsUp) + " " + this.errorMessage;
        return sStr;
    }
}



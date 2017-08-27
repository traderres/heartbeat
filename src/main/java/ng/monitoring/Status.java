package ng.monitoring;

/**
 * Created by adam on 8/26/17.
 */
public class Status
{
    private boolean siteIsUp;
    private String errorMessage;
    private String errorStep;
    private String entryDate;

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSiteUp() {
        return siteIsUp;
    }

    public void setSiteIsUp(boolean aSiteIsUp) {
        siteIsUp = aSiteIsUp;
    }


    public String getErrorStep() {
        return errorStep;
    }

    public void setErrorStep(String errorStep) {
        this.errorStep = errorStep;
    }
}

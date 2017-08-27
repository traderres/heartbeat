package ng.monitoring;

/**
 * Created by adam on 8/26/17.
 */
public class Status
{
    private boolean isSiteUp = false;
    private String errorMessage;
    private String errorStep;


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSiteUp() {
        return isSiteUp;
    }

    public void setSiteUp(boolean siteUp) {
        isSiteUp = siteUp;
    }


    public String getErrorStep() {
        return errorStep;
    }

    public void setErrorStep(String errorStep) {
        this.errorStep = errorStep;
    }

}

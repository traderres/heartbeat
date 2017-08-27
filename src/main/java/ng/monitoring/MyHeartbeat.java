package ng.monitoring;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyHeartbeat
{
    private static final Logger logger = LoggerFactory.getLogger(MyHeartbeat.class);



    /*********************************************************
     * main()
     *  1) Get the last status from the stats file
     *  2) Get the current status of the website
     *  3) Append the current status info to the stats file
     *  4) If the statuses are different, then
     *       If website is now down, then send website-is-down email
     *       If website is now up, then send website-is-up email
     *********************************************************/
    public static void main( String[] args )
    {
        try
        {
            logger.debug("main() started.");

            // Parse the command-line
            CommandLine cmd = parseCommandLine(args);

            // Get the stats file path from the command line
            String sStatsFilePath = cmd.getOptionValue("statsfile");

            if (cmd.hasOption("generatestats"))
            {
                // Generate Stats
                logger.debug("Generating Stats");
            }


            StatusFileUtils statusFileUtils = new StatusFileUtils(sStatsFilePath);

            // Get the last status info (from the stats file)
            Status lastStatusFromFile = statusFileUtils.getLastStatusFromStatsFile();

            // Get the current website status
            Status currentSiteStatus = getCurrentWebsiteStatus();

            // Append the current status to the stats file
            statusFileUtils.appendStatusToFile(currentSiteStatus);


            if ((lastStatusFromFile.isSiteUp() ) && (! currentSiteStatus.isSiteUp()  ))
            {
                // The website was last reported up and is now Down
                // -- Send the website-is-down notification
                logger.debug("Website is now down.");
            }
            else if ((! lastStatusFromFile.isSiteUp() ) && (currentSiteStatus.isSiteUp()  ))
            {
                // The website was last reported down and is now Up
                // -- Send the website-is-up notification
                logger.debug("Website is now up.");
            }
            else
            {
                // No change since the last time
                logger.debug("Website status is unchanged.  currentSiteStatus.isSiteUp={}", currentSiteStatus.isSiteUp());
            }
        }
        catch(Exception e)
        {
            logger.debug("Exception occurred", e);
        }
    }



   /*********************************************************
    * parseCommandLine()
    *********************************************************/
    private static CommandLine parseCommandLine(String[] args)
    {
        Options options = new Options();

        Option option1 = new Option("sf", "statsfile", true, "stats file path");
        option1.setArgs(1);         // The --statsfile has 1 argument that follows
        options.addOption(option1);

        Option option2 = new Option("gs", "generatestats", true, "generate stats email");
        option2.setRequired(false);
        option2.setArgs(0);         // The --generatestats has no argument that follows
        options.addOption(option2);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            return cmd;
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            formatter.printHelp(MyHeartbeat.class.toString(), options);

            System.exit(1);

            // Unreachable code -- but makes compiler happy
            return null;
        }
    }


    /*********************************************************
     * getCurrentDateTime()
     *********************************************************/
    public static String getCurrentDateTime()
     {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date now = new Date();
        String sDateTime = simpleDateFormat.format(now);
        return sDateTime;
    }


    /*********************************************************
     * getCurrentWebsiteStatus()
     *
     * 1) Attempt to connect to the website
     * 2) Return a Status object that holds information about whether the site is up or down
     *********************************************************/
    private static Status getCurrentWebsiteStatus()
    {
        logger.debug("getCurrentWebsiteStatus() started.");

        long lStartTime = System.currentTimeMillis();

        Status currentStatus = new Status();
        currentStatus.setSiteIsUp(false);
        currentStatus.setEntryDate( getCurrentDateTime() );

        try(WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45) )
        {

            String sURL = "https://localhost:8443/webapp1/search";
            InputStream clientCertInputStream = MyHeartbeat.class.getResourceAsStream("/clientCert.p12");
            String      clientCertPassword = "secret";
            String      clientCertType = "PKCS12";

            webClient.getOptions().setSSLClientCertificate(clientCertInputStream, clientCertPassword, clientCertType);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());

            // Connect to the JIMS Search Page
            final HtmlPage page = webClient.getPage(sURL);

            logger.debug("Waiting for bg javascript to finish...");
            webClient.waitForBackgroundJavaScriptStartingBefore(5000);
            logger.debug("Done waiting.");

            // Enter search criteria
            HtmlTextInput searchBox = (HtmlTextInput) page.getElementById("ctrl.searchBox");
            if (searchBox == null)
            {
                throw new RuntimeException("I did not find this button:  ctrl.searchBox");
            }
            logger.debug("Setting searchbox to hold 7571");
            searchBox.setText("7571");

            // Press the Search button
            HtmlInput searchButton = (HtmlInput) page.getElementById("searchBtn");
            logger.debug("Clicking on the searchButton");
            final HtmlPage page2 = searchButton.click();

            logger.debug("Waiting for bg javascript to finish...");
            webClient.waitForBackgroundJavaScriptStartingBefore(5000);
            logger.debug("Done waiting.");

            // Examine the number of results by looking at the inner HTML of a <span>...</span>
            HtmlElement spanTotalMatches = page2.getHtmlElementById("totalMatches");
            String sTotalMatches = spanTotalMatches.getTextContent();
            logger.debug("sTotalMatches={}", sTotalMatches);


            // Simulate pressing the logout button by making a Delete REST call
            WebRequest webRequest = new WebRequest(new URL("https://localhost:8443/fbi-web/logoutAngular"), HttpMethod.DELETE);
            webRequest.setAdditionalHeader("Accept", "*/*");
            webRequest.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            webRequest.setAdditionalHeader("Referer", "REFURLHERE");
            webRequest.setAdditionalHeader("Accept-Language", "en-US,en;q=0.8");
            webRequest.setAdditionalHeader("Accept-Encoding", "gzip,deflate,sdch");
            webRequest.setAdditionalHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
            webRequest.setAdditionalHeader("X-Requested-Wth", "XMLHttpRequest");
            webRequest.setAdditionalHeader("Cache-Control", "no-cache");
            webRequest.setAdditionalHeader("Pragma", "no-cache");

            Page redirectPage = webClient.getPage(webRequest);
            if( (redirectPage.getWebResponse() == null) || ((redirectPage.getWebResponse().getStatusCode() != 200)))
            {
                // Something went wrong logging out
                throw new RuntimeException("Something went wrong logging out");
            }

            // If I got this far, then the website is up
            currentStatus.setSiteIsUp(true);
        }
        catch (Exception e)
        {
            logger.debug("Exception raised:", e);
            currentStatus.setErrorMessage("something bad happened.");
       }

        long lEndTime = System.currentTimeMillis();
        long totalTimeInMilliseconds = lEndTime - lStartTime;

        logger.debug("Running Time is {} ms", totalTimeInMilliseconds);

        logger.debug("getCurrentWebsiteStatus() finished.");
        return currentStatus;
    }
}

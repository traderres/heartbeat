package ng.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by adam on 8/26/17.
 */
public class StatusFileUtils
{
    private final static Logger logger = LoggerFactory.getLogger(StatusFileUtils.class);

    private String statsFilePath;



   /********************************************************************
    * StatusFileUtils()  Constructor
    *
    *  1) If the stats file exists, then verify it is writable
    *  2) If the stats file does not exist, then create an empty one
    ********************************************************************/
    public StatusFileUtils(String aStatsFilePath) throws Exception
     {
        if ((aStatsFilePath == null) || (aStatsFilePath.length() == 0))
        {
            throw new RuntimeException("Error in StatusFileUtils():  The passed-in file path is null or empty.");
        }

        File fStatsFile = new File(aStatsFilePath);

        if (fStatsFile.exists() )
        {
            // The file exists.  But, can I write to it?
            if (! fStatsFile.canWrite() )
            {
                throw new RuntimeException("Error in StatusFileUtils():  This file exists, but I cannot write to it: " + aStatsFilePath);
            }
        }
        else
        {
            // The stats file does not exist -- so create an empty one
            logger.debug("The stats file is empty:  So, creating it:  {}", aStatsFilePath);
            fStatsFile.getParentFile().mkdirs();
            fStatsFile.createNewFile();
        }

        this.statsFilePath = aStatsFilePath;
    }



    /********************************************************************
     * appendStatusToFile()
     *
     * Append the status information to the Stats File
     ********************************************************************/
    public void appendStatusToFile(Status aStatus)
    {
        logger.debug("appendStatusToFile() started.");

        // Convert Status Object into a line of text (to append to the stats file)
        String sSummary = getLineOfTextFromStatus(aStatus);

        boolean bAppendMode = true;

        // Open the file in appendMode
        // Use the try-with-resources to open an OutputStreamWriter
        // NOTE:  When the JVM gets out of this try block, the OutputStreamWriter object will be closed
        try ( Writer fileWriter = new OutputStreamWriter(new FileOutputStream(this.statsFilePath, bAppendMode), StandardCharsets.UTF_8) )
        {
            // Append this line to the file
            fileWriter.write(sSummary + "\n");
        }
        catch(Exception e)
        {
            logger.debug("Critical Error in appendStatusToFile()", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace() );
            throw re;
        }

        logger.debug("appendStatusToFile() finished");
    }



    /********************************************************************
     * getLastStatusFromStatsFile()
     ********************************************************************/
    public Status getLastStatusFromStatsFile()
    {
        logger.debug("getLastStatusFromStatsFile() started.");

        Status lastStatus = null;


        // Use the try-with-resources to open a BufferedReader object
        // NOTE:  When the JVM gets out of this try block, the OutputStreamWriter object will be closed
        try ( BufferedReader br = new BufferedReader(new FileReader(this.statsFilePath)) )
        {
            String sLine = null;
            String sLastLine = null;
            while ((sLine = br.readLine()) != null)
            {
                // Read every line in the file
                sLastLine = sLine;
            }

            // If the file is empty, then the last line might be null.  That's OK

            // Get the status object info from this line of text
            lastStatus = getStatusObjectFromLineOfText(sLastLine);

            // Get the status info from the line
            logger.debug("last line is this: {}", sLastLine);
        }
        catch (Exception e)
        {
            logger.debug("Critical Error in getLastStatusFromStatsFile()", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace() );
            throw re;
        }

        logger.debug("getLastStatusFromStatsFile() finished.");
        return lastStatus;
    }


    /********************************************************************
     * getSummary()
     *   1) Get the total amount of down-time over the last week (168 hours)
     *   2) Get the total amount of down-time since day 1
     *   3) Return a string that shows
     *
     *         Since last Sunday
     *         ------------------
     *            Date Range:            08/27/2017 - 09/02/2017   [last Sunday before last Saturday - last Saturday]
     *            Total Down Time:       1 hr, 34 min, 32 secs
     *            Total Time:            7 days
     *            Uptime as a percent:   98.55%                    (total down time / total time)  * 100
     *
     *
     *         Since Day 1
     *         -----------
     *            Date Range:            08/27/2017 -            [1st date in stats - last date in stats file]
     *            Total Down Time:       1 hr, 34 min, 32 secs
     *            Total Time:            254 days, 167 hr, 26 min
     *            Uptime as a percent:   98.55%                  (total down time / total time)  * 100
     ********************************************************************/
    public String getSummary()
    {
        logger.debug("getSummary() started.");

        long lWeekStartDate = DateUtils.getSundayMidnightBeforeLastSaturdayAsEpoch();
        long lWeekEndDate = DateUtils.getLastSaturdayMignightAsEpoch();


        StringBuilder sbSummary = new StringBuilder();

        // Use the try-with-resources to read from the file (using a BufferedReader object)
        // NOTE:  When the JVM gets out of this try block, the OutputStreamWriter object will be closed
        try ( BufferedReader br = new BufferedReader(new FileReader(this.statsFilePath)) )
        {
            String sLine = null;
            while ((sLine = br.readLine()) != null)
            {

            }


            // Get the status info from the line
            logger.debug("last line is this: {}", sLine);
        }
        catch (Exception e)
        {
            logger.debug("Critical Error in getLastStatusFromStatsFile()", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace() );
            throw re;
        }

        logger.debug("getSummary() finished.");
        return sbSummary.toString();
    }

    /********************************************************************
     * getStatusObjectFromLineOfText()
     *
     * Returns a Null object if no status info was found in the file (the file was empty)
     ********************************************************************/
    private Status getStatusObjectFromLineOfText(String aLine)
    {

        if ((aLine == null) || (aLine.length() == 0))
        {
            // The line was empty so return an *empty* status object
            return null;
        }

         Status oStatus = new Status();

        // Split-up the line into its elements
        String[] statsArray = aLine.split(",");

        String  sEntryDate = statsArray[0];
        Boolean bSiteIsUp = new Boolean(statsArray[1]);
        String  sErrorMessage = statsArray[2];
        String  sErrorStep = statsArray[3];

        oStatus.setEntryDate(sEntryDate);
        oStatus.setSiteIsUp(bSiteIsUp);
        oStatus.setErrorMessage(sErrorMessage);
        oStatus.setErrorStep(sErrorStep);

        return oStatus;
    }



    /********************************************************************
     * getLineOfTextFromStatus()
     ********************************************************************/
    private String getLineOfTextFromStatus(Status aStatus)
    {
        String sStatsLine = aStatus.getEntryDate() + "," +
                            aStatus.isSiteUp() + "," +
                            aStatus.getErrorMessage() + "," +
                            aStatus.getErrorStep();

        return sStatsLine;
    }

}

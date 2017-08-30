package ng.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adam on 8/26/17.
 */
public class StatusFileUtils
{
    private final static Logger logger = LoggerFactory.getLogger(StatusFileUtils.class);

    private String statsFilePath;

    private static Pattern patExtractTimeAndSiteIsUp = Pattern.compile("^(.*?),(.*?),");

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
        String sSummary = getLineOfTextFromStatusObject(aStatus);

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
            String sLine;
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
     * getSummarySinceDay1()
     *
     * Return a string that holds this:
     *
     *         Stats Since Day 1
     *         -----------
     *            Date Range:            08/27/2016 - 09/27/2017         [1st date in stats - last date in stats file]
     *            Total Down Time:       1 hr, 34 min, 32 secs
     *            Total Up Time:         253 days, 15 hr
     *            Total Time:            254 days, 16 hr, 26 min
     *            Uptime as a percent:   98.55%                  (total down time / total time)  * 100
     ********************************************************************/
    public String getSummarySinceDay1()
    {
        logger.debug("getSummarySinceDay1() started.");

        String sSummary;

        long lFirstEntryDateAsEpoch = 0;
        long lLastEntryDateAsEpoch = 0;

        long lTotalUpTime = 0;
        long lTotalDownTime = 0;
        long lTotalTime;

        long lDateStartingUpTime = 0;

        // Use the try-with-resources to read from the file (using a BufferedReader object)
        // NOTE:  When the JVM gets out of this try block, the OutputStreamWriter object will be closed
        try ( BufferedReader br = new BufferedReader(new FileReader(this.statsFilePath)) )
        {
            // Read one line from the file
            String sLine = br.readLine();
            BriefStatus statsLine = getBriefStatusFromLine(sLine);

            // The startDate is the epoch date pulled from the first line
            lFirstEntryDateAsEpoch = statsLine.getlDateAsEpoch();


            // Now, we have reached the desired range
            BriefStatus lastStatsLine = statsLine;

            if (lastStatsLine.isbSiteUp())
            {
                 lDateStartingUpTime = lastStatsLine.getlDateAsEpoch();
            }

            // Keep reading until we reach EOF or the end of the range
            while ((sLine = br.readLine()) != null)
            {
                statsLine = getBriefStatusFromLine(sLine);

                logger.debug("Processing statsLine={}   lastStatsLine={}", statsLine.toString(), lastStatsLine.toString());

                if ((lastStatsLine.isbSiteUp()) && (! statsLine.isbSiteUp() ))
                {
                    // last stats was up and current stats is down
                    lTotalUpTime = lTotalUpTime + lastStatsLine.getlDateAsEpoch() - lDateStartingUpTime + 1;
                }
                else if ((! lastStatsLine.isbSiteUp()) && (statsLine.isbSiteUp()))
                {
                    // last stats was down and current stats is up -- so reset the count
                    lDateStartingUpTime = statsLine.getlDateAsEpoch();
                }

                lLastEntryDateAsEpoch = statsLine.getlDateAsEpoch();
                lastStatsLine = statsLine;
            }


            // We have reached the end of the range
            // -- Add up any numbers
            if (lastStatsLine.isbSiteUp())    //  && (! statsLine.isbSiteUp() ))
            {
                // last stats was up and current stats is down
                lTotalUpTime = lTotalUpTime + lastStatsLine.getlDateAsEpoch() - lDateStartingUpTime + 1;
            }


            lTotalTime = lLastEntryDateAsEpoch - lFirstEntryDateAsEpoch + 1;
            lTotalDownTime = lTotalTime - lTotalUpTime;
            float percentUpTime =  ((float) lTotalUpTime / lTotalTime) * 100;

            sSummary = String.format("Date Range:         %s - %s\n" +
                                     "Total Down Time:    %s\n" +
                                     "Total Up Time:      %s\n" +
                                     "Total Time:         %s\n" +
                                     "Percent Uptime:     %.2f%%\n",
                                     DateUtils.getDateOfEpochTime(lFirstEntryDateAsEpoch),
                                     DateUtils.getDateOfEpochTime(lLastEntryDateAsEpoch),
                                     DateUtils.getHumanReadableTimeFromSecs(lTotalDownTime),
                                     DateUtils.getHumanReadableTimeFromSecs(lTotalUpTime),
                                     DateUtils.getHumanReadableTimeFromSecs(lTotalTime),
                                     percentUpTime);
        }
        catch (Exception e)
        {
            logger.debug("Critical Error in getLastStatusFromStatsFile()", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace() );
            throw re;
        }

        logger.debug("getSummarySinceDay1() finished.\n{}", sSummary);
        return sSummary;
    }



    /********************************************************************
     * getSummarySinceLastSunday()
     *
     * Return a string that holds this:
     *
     *         Stats Since last Sunday
     *         ------------------
     *            Date Range:            08/27/2017 - 09/02/2017   [last Sunday before last Saturday - last Saturday]
     *            Total Down Time:       1 hr, 34 min, 32 secs
     *            Total Up Time:         7 days, 1 hr, 16 min
     *            Total Time:            7 days
     *            Uptime as a percent:   98.55%                    (total uptime / total time)  * 100
     ********************************************************************/
    public String getSummarySinceLastSunday()
    {
        logger.debug("getSummarySinceLastSunday() started.");

        // Get the epoch values for last Sunday and last Saturday
        long lDateRangeStartAsEpoch = DateUtils.getSundayMidnightBeforeLastSaturdayAsEpoch();
        long lDateRangeEndAsEpoch = DateUtils.getLastSaturdayMignightAsEpoch();

        String sSummary;

        long lFirstEntryDateAsEpoch = 0;
        long lLastEntryDateAsEpoch = 0;

        long lTotalUpTime = 0;
        long lTotalDownTime = 0;
        long lTotalTime = 0;

        long lDateStartingUpTime = 0;

        // Use the try-with-resources to read from the file (using a BufferedReader object)
        // NOTE:  When the JVM gets out of this try block, the OutputStreamWriter object will be closed
        try ( BufferedReader br = new BufferedReader(new FileReader(this.statsFilePath)) )
        {
            String sLine;

            BriefStatus statsLine = null;

            // Keep reading until we reach the EOF or the desired time period
            while ((sLine = br.readLine()) != null)
            {
                statsLine = getBriefStatusFromLine(sLine);

                if (statsLine.getlDateAsEpoch() > lDateRangeEndAsEpoch)
                {
                    // Break out of the while loop -- we are passed the date range
                    logger.warn("There are is nothing in the range");
                    return "No information is available";
                }
                else if (statsLine.getlDateAsEpoch() >= lDateRangeStartAsEpoch)
                {
                    // We reached the starting point -- break out of this while loop
                    logger.debug("Reached the starting point.");
                    lFirstEntryDateAsEpoch = statsLine.getlDateAsEpoch();
                    break;
                }
            }

            // Now, we have reached the desired range
            BriefStatus lastStatsLine = statsLine;

            if (lastStatsLine.isbSiteUp())
            {
                 lDateStartingUpTime = lastStatsLine.getlDateAsEpoch();
            }

            // Keep reading until we reach EOF or the end of the range
            while ((sLine = br.readLine()) != null)
            {
                statsLine = getBriefStatusFromLine(sLine);

                logger.debug("Processing statsLine={}   lastStatsLine={}", statsLine.toString(), lastStatsLine.toString());
                if (statsLine.getlDateAsEpoch() > lDateRangeEndAsEpoch)
                {
                    // We have reached the end of the range
                    // -- Add up any numbers

                   if (lastStatsLine.isbSiteUp())    //  && (! statsLine.isbSiteUp() ))
                   {
                        // last stats was up and current stats is down
                        lTotalUpTime = lTotalUpTime + lastStatsLine.getlDateAsEpoch() - lDateStartingUpTime + 1;
                   }

                   // Break out of the while loop
                   break;
                }
                else if ((lastStatsLine.isbSiteUp()) && (! statsLine.isbSiteUp() ))
                {
                    // last stats was up and current stats is down
                    lTotalUpTime = lTotalUpTime + lastStatsLine.getlDateAsEpoch() - lDateStartingUpTime + 1;
                }
                else if ((! lastStatsLine.isbSiteUp()) && (statsLine.isbSiteUp()))
                {
                    // last stats was down and current stats is up -- so reset the count
                    lDateStartingUpTime = statsLine.getlDateAsEpoch();
                }

                lLastEntryDateAsEpoch = statsLine.getlDateAsEpoch();
                lastStatsLine = statsLine;
            }



            lTotalTime = lLastEntryDateAsEpoch - lFirstEntryDateAsEpoch + 1;
            lTotalDownTime = lTotalTime - lTotalUpTime;
            float percentUpTime =  ((float) lTotalUpTime / lTotalTime) * 100;

            sSummary = String.format("Date Range:         %s - %s\n" +
                                     "Total Down Time:    %s\n" +
                                     "Total Up Time:      %s\n" +
                                     "Total Time:         %s\n" +
                                     "Percent Uptime:     %.2f%%\n",
                                     DateUtils.getDateOfEpochTime(lDateRangeStartAsEpoch),
                                     DateUtils.getDateOfEpochTime(lDateRangeEndAsEpoch),
                                     DateUtils.getHumanReadableTimeFromSecs(lTotalDownTime),
                                     DateUtils.getHumanReadableTimeFromSecs(lTotalUpTime),
                                     DateUtils.getHumanReadableTimeFromSecs(lTotalTime),
                                     percentUpTime);
        }
        catch (Exception e)
        {
            logger.debug("Critical Error in getLastStatusFromStatsFile()", e);
            RuntimeException re = new RuntimeException(e);
            re.setStackTrace(e.getStackTrace() );
            throw re;
        }

        logger.debug("getSummarySinceLastSunday() finished.\n{}", sSummary);
        return sSummary;
    }


    /********************************************************************
     * getBriefStatusFromLine()
     ********************************************************************/
    private BriefStatus getBriefStatusFromLine(String aLine)
    {
        Matcher m = patExtractTimeAndSiteIsUp.matcher(aLine);

        if (! m.find() )
        {
            throw new RuntimeException("Critical Error in getDateFromLine()  I could not parse the date from this line:  aLine=" + aLine);
        }

        long lDateFromLine = Long.parseLong(m.group(1));
        boolean bSiteIsUpFromLine = Boolean.parseBoolean(m.group(2));

        BriefStatus briefStatus = new BriefStatus(lDateFromLine, bSiteIsUpFromLine);
        return briefStatus;
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
     * getLineOfTextFromStatusObject()
     ********************************************************************/
    private String getLineOfTextFromStatusObject(Status aStatus)
    {
        if (aStatus == null)
        {
            throw new RuntimeException("Critical Error in getLineOfTextFromStatusObject():  The passed-in aStatus object is null.");
        }

        String sStatsLine = aStatus.getEntryDate() + "," +
                            aStatus.isSiteUp() + "," +
                            aStatus.getErrorMessage() + "," +
                            aStatus.getErrorStep();

        return sStatsLine;
    }

}

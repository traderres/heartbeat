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
            while ((sLine = br.readLine()) != null)
            {

            }

            lastStatus = new Status();

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


        logger.debug("getLastStatusFromStatsFile() finished.");
        return lastStatus;
    }


    /********************************************************************
     * getStatusSummaryFromFile()
     ********************************************************************/
    public String getStatusSummaryFromFile()
    {
        String sSummary = "blah";

        return sSummary;
    }

   /********************************************************************
     * appendStatusToFile()
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
            logger.debug("The stats file is empty:  So, creating it.");
            fStatsFile.getParentFile().mkdirs();
            fStatsFile.createNewFile();
        }

        this.statsFilePath = aStatsFilePath;
    }



    /********************************************************************
     * appendStatusToFile()
     ********************************************************************/
    public void appendStatusToFile(Status aStatus)
    {
        logger.debug("appendStatusToFile() started.");


        String sSummary = "this is the summary";

        // Use the try-with-resources to open an OutputStreamWriter
        // NOTE:  When the JVM gets out of this try block, the OutputStreamWriter object will be closed
        try ( Writer fileWriter = new OutputStreamWriter(new FileOutputStream(this.statsFilePath), StandardCharsets.UTF_8) )
        {
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

}

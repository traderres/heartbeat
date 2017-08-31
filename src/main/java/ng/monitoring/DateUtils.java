package ng.monitoring;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by adam on 8/28/17.
 */
public class DateUtils
{

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
     * getCurrentDate()
     *********************************************************/
    public static String getCurrentDate()
     {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date now = new Date();
        String sDate = simpleDateFormat.format(now);
        return sDate;
    }

    /*********************************************************
     * getCurrentDateTimeAsEpochString()
     *********************************************************/
    public static String getCurrentDateTimeAsEpochString()
    {
        long lDateTimeAsSecondsSince1970 = Instant.now().getEpochSecond();

        String sDateTimeAsSecondsSince1970 = java.lang.String.valueOf(lDateTimeAsSecondsSince1970);

        return sDateTimeAsSecondsSince1970;
    }


    /*********************************************************
     * getCurrentDateTimeAsEpochLong()
     *********************************************************/
    public static long getCurrentDateTimeAsEpochLong()
    {
        long lDateTimeAsSecondsSince1970 = Instant.now().getEpochSecond();
        return lDateTimeAsSecondsSince1970;
    }


    /*********************************************************
     * getMidnightAsEpoch()
     *********************************************************/
    public static long getMidnightAsEpoch()
    {
        TimeZone timezone = Calendar.getInstance().getTimeZone();
        Calendar c = Calendar.getInstance(timezone);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long lMidnightAsEpoch = c.getTimeInMillis() / 1000;
        return lMidnightAsEpoch;
    }


    /*********************************************************
     * getLastSundayMidnightAsEpoch()
     *********************************************************/
    public static long getLastSundayMidnightAsEpoch()
    {
        TimeZone timezone = Calendar.getInstance().getTimeZone();
        Calendar c = Calendar.getInstance(timezone);

        // Move the calendar back to last Sunday
        int daysBackToSunday = c.get(Calendar.DAY_OF_WEEK ) - 1;
        c.add(Calendar.DATE, daysBackToSunday * -1);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long lLastSundayMidnightAsEpoch = c.getTimeInMillis() / 1000;
        return lLastSundayMidnightAsEpoch;
    }

    /*********************************************************
     * getLastSaturdayMignightAsEpoch()
     *********************************************************/
    public static long getLastSaturdayMignightAsEpoch()
    {
        TimeZone timezone = Calendar.getInstance().getTimeZone();
        Calendar c = Calendar.getInstance(timezone);

        // Move the calendar back to last Saturday
        int daysBackToSat = c.get(Calendar.DAY_OF_WEEK );
        c.add(Calendar.DATE, daysBackToSat * -1);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long lLastSaturdayMidnightAsEpoch = c.getTimeInMillis() / 1000;
        return lLastSaturdayMidnightAsEpoch;
    }



    /*********************************************************
     * getSundayMidnightBeforeLastSaturdayAsEpoch()
     *********************************************************/
    public static long getSundayMidnightBeforeLastSaturdayAsEpoch()
    {
        TimeZone timezone = Calendar.getInstance().getTimeZone();
        Calendar c = Calendar.getInstance(timezone);

        int daysBackToSunday = c.get(Calendar.DAY_OF_WEEK ) - 1;
        daysBackToSunday = daysBackToSunday + 7;

        // Move the calendar back to Sunday before last
        c.add(Calendar.DATE, daysBackToSunday * -1);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long lLastSundayMidnightAsEpoch = c.getTimeInMillis() / 1000;
        return lLastSundayMidnightAsEpoch;
    }

    /*********************************************************
     * getDateOfEpochTime()
     *********************************************************/
    public static String getDateOfEpochTime(long aEpochTime)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date passedInDate = new Date( aEpochTime * 1000 );
        String sDateTime = simpleDateFormat.format(passedInDate);
        return sDateTime;
    }

    /*********************************************************
     * getHumanReadableTimeFromSecs()
     *********************************************************/
    public static String getHumanReadableTimeFromSecs(long aTotalSecs)
    {
        if (aTotalSecs == 0)
        {
            return "0 secs";
        }

        int elapsedYears   = (int) (aTotalSecs / 31536000);    // assume 365 days/year
		int elapsedDays    = (int) (aTotalSecs / 86400);
		int elapsedHours   = (int) (aTotalSecs / 3600);
		int elapsedMinutes = (int) (aTotalSecs / 60);
        int elapsedSeconds = (int) (aTotalSecs % 60);

        StringBuilder sb = new StringBuilder();
        if (elapsedYears > 1)
        {
            sb.append(elapsedYears + " years, ");
        }

        if (elapsedDays > 0)
        {
            sb.append(elapsedDays + " days, ");
        }

        if (elapsedHours > 0)
        {
            sb.append(elapsedHours + " hours, ");
        }

        if (elapsedMinutes > 0)
        {
            sb.append(elapsedMinutes + " min, ");
        }

        if (elapsedSeconds > 0)
        {
            sb.append(elapsedSeconds + " secs");
        }

        // Remove the last comma (if needed)
        if (sb.charAt(sb.length() - 1) == ',')
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

}

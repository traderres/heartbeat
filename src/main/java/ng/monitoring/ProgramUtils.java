package ng.monitoring;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;

/**
 * Created by adam on 8/31/17.
 */
public class ProgramUtils
{


    /*******************************************************************
     * getHostname()
     *
     *******************************************************************/
    public static String getHostname()
    {
        String hostname = "Unknown";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            hostname = "Unkonwn-hostname";
        }

        return hostname;
    }


    /*******************************************************************
     * getMainClassName()
     *
     *******************************************************************/
    public static String getMainClassName()
    {
        StackTraceElement[] stack = Thread.currentThread ().getStackTrace ();
        StackTraceElement main = stack[stack.length - 1];
        String sMainClassName = main.getClassName();

        return sMainClassName;
    }


    /*******************************************************************
     * getCurrentWorkingDirectory()
     *
     *******************************************************************/
    public static String getCurrentWorkingDirectory()
    {
        String sCWD = Paths.get(".").toAbsolutePath().normalize().toString();
        return sCWD;
    }
}

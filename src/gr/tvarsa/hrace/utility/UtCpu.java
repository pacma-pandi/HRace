package gr.tvarsa.hrace.utility;

public class UtCpu
{
    public static boolean pause(long milliseconds)
    {
        if (milliseconds < 1) return true;
        try
        {
            int priority = Thread.currentThread().getPriority();
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            Thread.sleep(milliseconds);
            Thread.currentThread().setPriority(priority);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static boolean pause(double seconds)
    {
        return pause((int)(seconds * 1000));
    }

}

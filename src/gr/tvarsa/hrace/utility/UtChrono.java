package gr.tvarsa.hrace.utility;

import gr.tvarsa.hrace.utility.UtString;

public class UtChrono
{
    private long startTime = 0;
    private long elapsedTime = 0;
    private boolean running = false;

    public void start()
    {
        startTime = System.nanoTime();
        elapsedTime = 0;
        running = true;
    }

    public void reset()
    {
        startTime = System.nanoTime();
        elapsedTime = 0;
        running = false;
    }

    public void restart()
    {
        startTime = System.nanoTime();
        elapsedTime = 0;
        running = true;
    }

    public void stop()
    {
        if (!running) return;
        elapsedTime = elapsedTime + System.nanoTime() - startTime;
        running = false;
    }

    public void pause()
    {
        stop();
    }

    public void resume()
    {
        if (running) return;
        startTime = System.nanoTime();
        running = true;
    }

    private long currentTime()
    {
        if (running)
            return elapsedTime + System.nanoTime() - startTime;
        return elapsedTime;
    }

    public double getSeconds()
    {
        return currentTime() / 1000000000.0;
    }

    public double getMilliseconds()
    {
        return currentTime() / 1000000.0;
    }

    public String getSecondsStr(int decimals)
    {
        return UtString.roundStr(getSeconds(), decimals);
    }

    public String getMillisecondsStr(int decimals)
    {
        return UtString.roundStr(getMilliseconds(), decimals);
    }

    public UtChrono()
    {
        this(false);
    }

    public UtChrono(boolean startTimer)
    {
        if (startTimer)
            start();
        else
            reset();
    }
}

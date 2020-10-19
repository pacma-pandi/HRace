package gr.tvarsa.hrace.utility;

import java.util.Random;

public class UtMath
{
    private static Random randomGenerator = new Random();
    private static long seed = -1;

    public static int rnd(int min, int max)
    {
        return (int)(randomGenerator.nextDouble() * (max - min + 1)) + min;
    }

    public static double rnd(double min, double max)
    {
        return randomGenerator.nextDouble() * (max - min) + min;
    }

    public static double rnd()
    {
        return randomGenerator.nextDouble();
    }

    public static boolean inRange(double value, double min, double max)
    {
        return value >= min && value <= max;
    }

    /**
     * Round double number to the set number of decimal digits.
     *
     * @param num
     *            double number to round
     * @param decimals
     *            number of decimal digits
     * @return rounded number
     */
    public static double round(double num, int decimals)
    {
        double pow10 = Math.pow(10, decimals);
        return Math.round(num * pow10) / pow10;
    }

    public static int ranged(int value, int min, int max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double ranged(double value, double min, double max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static int warpRange(int value, int min, int max)
    {
        if (value < min) value = max - (min - value - 1);
        if (value > max) value = min + value - max - 1;
        return value;
    }
    public static long getRandomSeed()
    {
        return seed;
    }

    /**
     * Always returns <code>true</code>. Used for suppressing Eclipse warnings at conditions which it assumes will always return
     * either true or false, thus making subsequent code segments obsolete.
     */
    public static boolean isTrue()
    {
        return true;
    }

    /**
     * Returns the evaluation of the supplied expression. Used for suppressing Eclipse warnings at conditions which it assumes
     * will always return either true or false, thus making subsequent code segments obsolete.
     */
    public static boolean isTrue(boolean b)
    {
        return b;
    }
}

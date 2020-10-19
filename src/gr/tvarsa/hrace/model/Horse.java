package gr.tvarsa.hrace.model;

import gr.tvarsa.hrace.animate.HorseImage;
import gr.tvarsa.hrace.animate.HorseImageColors;
import gr.tvarsa.hrace.gui.CameraPanel;
import gr.tvarsa.hrace.utility.UtImage;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Horse
{
    private static final String[] itemLines = {// "number", //
            // "xPos", //
            "yPosHeightDrift", //
            // "maxStamina", //
            // "minStamina", //
            // "stamina", //
            // "speed", //
            // "minCurrSpeed", //
            // "maxCurrSpeed", //
            // "minRunSpeed", //
            // "maxRunSpeed", //
            // "images", //
            // "distancePerGallup", //
            // "distanceInGallup", //
            // "currentImageIndex", //
            // "movedThisRound", //
            // "finishOrder", //
            // "finished", //
            // "finishTime", //
            // "injured", //
            // "randomImageShift", //
            // "startDelay", //
            // "winPayoff", //
            // "DNF", //
            "DSQ", //
            // "status"//
    };
    private static List<Image> unprocessedImages = new ArrayList<>();
    private static Image unprocessedStoppedImage;

    private int number;
    private double xPos;
    /** How much up or down in horse heights is the horse from its expected y position (1=a full horse height). */
    private double yPosHeightDrift;
    private double maxStamina;
    private double minStamina;
    private double stamina;
    private double speed;
    private double minCurrSpeed;
    private double maxCurrSpeed;
    private double minRunSpeed;
    private double maxRunSpeed;
    private List<HorseImage> images = new ArrayList<>();
    private double distancePerGallup;
    private double distanceInGallup;
    private int currentImageIndex;
    private boolean movedThisRound;
    private int finishOrder;
    private boolean finished;
    private double finishTime;
    private boolean injured;
    private int randomImageShift;
    private double startDelay;
    private double winPayoff;
    private boolean DNF;
    private boolean DSQ;
    private String itemValues[] = new String[itemLines.length];

    private static final int SPEED_AVERAGES = 70;

    private double speeds[] = new double[SPEED_AVERAGES];
    private int nextSpeedLocation = 0;

    public static void loadImages()
    {
        int index = -1;
        while (true)
        {
            index++;
            String filename = "gallop" + UtString.padInt(index, 2) + ".png";
            Image image = UtImage.loadImage("gr.tvarsa.hrace.app.HorjeRace.jar", filename, true);
            if (image == null)
            {
                if (index >= 1) break;
                continue;
            }
            if (index > 0)
                unprocessedImages.add(image);
            else
                unprocessedStoppedImage = image;
        }
    }

    public static int countImages()
    {
        return unprocessedImages.size();
    }

    public static int getImageWidth()
    {
        if (unprocessedImages.size() == 0) return 0;
        return unprocessedImages.get(0).getWidth(null);
    }

    public static int getImageHeight()
    {
        if (unprocessedImages.size() == 0) return 0;
        return unprocessedImages.get(0).getHeight(null);
    }

    public Horse()
    {
        setRandomPhysique();
        setRandomFitness();
    }

    public void createImages()
    {
        images = new ArrayList<>();
        HorseImageColors horseImageColors = new HorseImageColors();
        images.add(new HorseImage(unprocessedStoppedImage, horseImageColors));
        for (int i = 0; i < unprocessedImages.size(); i++)
            images.add(new HorseImage(unprocessedImages.get(i), horseImageColors));
        randomImageShift = UtMath.rnd(0, unprocessedImages.size() - 1 - 1);
    }

    public int getRandomImageShift()
    {
        return randomImageShift;
    }

    public HorseImage getStoppedImage()
    {
        if (images == null || images.size() == 0) return null;
        return images.get(0);
    }

    public HorseImage getCurrentImage()
    {
        if (images == null || images.size() == 0) return null;
        return images.get(currentImageIndex);
    }

    public HorseImage getNextImage()
    {
        if (images == null || images.size() == 0) return null;
        currentImageIndex++;
        if (currentImageIndex >= images.size()) currentImageIndex = 1;
        return images.get(currentImageIndex);
    }

    public HorseImage getImage(int i)
    {
        if (images == null || images.size() == 0 || i < 0 || i >= images.size()) return null;
        return images.get(i);
    }

    public HorseImage getRandomImage()
    {
        if (images == null || images.size() == 0) return null;
        currentImageIndex = UtMath.rnd(1, images.size() - 1);
        return images.get(currentImageIndex);
    }

    public void setCurrentImageIndex(int currentImageIndex)
    {
        this.currentImageIndex = UtMath.ranged(currentImageIndex, 0, images.size() - 1);
    }

    public int getCurrentImageIndex()
    {
        return currentImageIndex;
    }

    public double getFaceX()
    {
        if (images.size() == 0) return xPos;
        double x2 = CameraPanel.horseWidth * images.get(currentImageIndex).getX2() / images.get(currentImageIndex).getWidth();
        return xPos + x2;
    }

    public void setRandomFitness()
    {
        setStartDelay(UtMath.rnd(0.0, 200.0));
    }

    public void setRandomPhysique()
    {
        setMinRunSpeed(UtMath.rnd(50.0, 70.0));
        setMaxRunSpeed(UtMath.rnd(70.0, 100.0));
        setMinCurrSpeed(0);
        setMaxCurrSpeed(getMaxRunSpeed());
        setSpeed(getMinCurrSpeed());
        setMinStamina(UtMath.rnd(60.0, 80.0));
        setMaxStamina(UtMath.rnd(80.0, 100.0));
        setStamina(getMaxStamina());
        setDistancePerGallup(CameraPanel.horseWidth * 1.5 + UtMath.rnd(0.0, 0.2));
        distanceInGallup = 0;
    }

    public void prepareForRace()
    {
        setMovedThisRound(false);
        setFinishOrder(0);
        setFinished(false);
        setFinishTime(0);
        setInjured(false);
        setDNF(false);
        setDSQ(false);
        setXPos(-(getFaceX() - getXPos()));
        setYPosHeightDrift(0);
    }

    public double getDistancePerGallup()
    {
        return distancePerGallup;
    }

    public void setDistancePerGallup(double distancePerGallup)
    {
        this.distancePerGallup = distancePerGallup;
    }

    public double getStartDelay()
    {
        return startDelay;
    }

    public void setStartDelay(double startDelay)
    {
        this.startDelay = startDelay;
    }

    public boolean isInjured()
    {
        return injured;
    }

    public void setInjured(boolean injured)
    {
        this.injured = injured;
    }

    public double getSpeed()
    {
        return speed;
    }

    public void setSpeed(double speed)
    {
        this.speed = UtMath.ranged(speed, minCurrSpeed, maxCurrSpeed);
        speeds[nextSpeedLocation] = this.speed;
        nextSpeedLocation++;
        if (nextSpeedLocation >= speeds.length) nextSpeedLocation = 0;

    }

    public double getWinPayoff()
    {
        return winPayoff;
    }

    public void setWinPayoff(double winPayoff)
    {
        this.winPayoff = UtMath.round(winPayoff, 2);
    }

    public double getMinRunSpeed()
    {
        return minRunSpeed;
    }

    public void setMinRunSpeed(double minRunSpeed)
    {
        this.minRunSpeed = minRunSpeed;
    }

    public double getMaxRunSpeed()
    {
        return maxRunSpeed;
    }

    public void setMaxRunSpeed(double maxRunSpeed)
    {
        this.maxRunSpeed = maxRunSpeed;
    }

    public double getMinCurrSpeed()
    {
        return minCurrSpeed;
    }

    public void setMinCurrSpeed(double minCurrSpeed)
    {
        this.minCurrSpeed = minCurrSpeed;
        setSpeed(speed);
    }

    public double getMaxCurrSpeed()
    {
        return maxCurrSpeed;
    }

    public void setMaxCurrSpeed(double maxCurrSpeed)
    {
        this.maxCurrSpeed = maxCurrSpeed;
        setSpeed(speed);
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public double getStamina()
    {
        return stamina;
    }

    public void setStamina(double stamina)
    {
        this.stamina = UtMath.ranged(stamina, minStamina, maxStamina);
    }

    public double getMinStamina()
    {
        return minStamina;
    }

    public void setMinStamina(double minStamina)
    {
        this.minStamina = minStamina;
    }

    public double getMaxStamina()
    {
        return maxStamina;
    }

    public void setMaxStamina(double maxStamina)
    {
        this.maxStamina = maxStamina;
    }

    public double getXPos()
    {
        return xPos;
    }

    public void setXPos(double pos)
    {
        xPos = pos;
    }

    public void addXPos(double x)
    {
        xPos += x;
        distanceInGallup += x;
        if (distanceInGallup > getDistancePerGallup() / countImages())
        {
            distanceInGallup = distanceInGallup % (getDistancePerGallup() / countImages());
            getNextImage();
        }
    }

    /** How much up or down in horse heights is the horse from its expected y position (1=a full horse height). */
    public double getYPosHeightDrift()
    {
        return yPosHeightDrift;
    }

    /** How much up or down in horse heights is the horse from its expected y position (1=a full horse height). */
    public void setYPosHeightDrift(double drift)
    {
        yPosHeightDrift = drift;
    }

    public boolean isMovedThisRound()
    {
        return movedThisRound;
    }

    public void setMovedThisRound(boolean movedThisRound)
    {
        this.movedThisRound = movedThisRound;
    }

    public int getFinishOrder()
    {
        return finishOrder;
    }

    public void setFinishOrder(int finishOrder)
    {
        this.finishOrder = finishOrder;
    }

    public boolean isFinished()
    {
        return finished;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    public double getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(double finishTime)
    {
        this.finishTime = finishTime;
    }

    public boolean isDNF()
    {
        return DNF;
    }

    public void setDNF(boolean dnf)
    {
        DNF = dnf;
    }

    public boolean isDSQ()
    {
        return DSQ;
    }

    public void setDSQ(boolean dsq)
    {
        DSQ = dsq;
    }

    public boolean paysBet()
    {
        return !DNF && !DSQ;
    }

    public double getPerformance()
    {
        double avgSpeed = (maxRunSpeed + minRunSpeed) / 2;
        double avgStamina = (maxStamina + minStamina) / 2;
        return UtMath.round((2 * avgSpeed + avgStamina) / 3.0, 1);
    }

    public int getLevel()
    {
        return UtMath.ranged((int)((getPerformance() - 83) / 2.0 + 10), Engine.MIN_HORSE_LEVEL, Engine.MAX_HORSE_LEVEL);
    }

    public void paintInfoAt(Graphics2D g2d, int x, int y, int w, int h)
    {
        if (currentImageIndex <= 0) return;
        Rectangle r = new Rectangle(x, y, w, h);
        g2d.setColor(Color.yellow);
        g2d.fill(r);
        g2d.setColor(Color.black);
        g2d.draw(r);
        int lineHeight = h / itemLines.length;
        g2d.setFont(new Font("Tahoma", Font.PLAIN, (int)(lineHeight * 0.6)));
        int widthSplit = (int)(w * 0.6);
        int i = 0;
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( number,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(xPos, 2);
        itemValues[i++] = UtString.roundStr(yPosHeightDrift, 2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( maxStamina,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( minStamina,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(stamina, 2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(getSpeedAverage(), 2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( minCurrSpeed,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( maxCurrSpeed,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( minRunSpeed,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( maxRunSpeed,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( images,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(distancePerGallup, 2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(distanceInGallup, 2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( currentImageIndex,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( movedThisRound,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( finishOrder,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( finished,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(finishTime, 2);
        // itemValues[i++] = "" + injured;
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( randomImageShift,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr(startDelay, 2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( winPayoff,2);
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( DNF,2);
        // itemValues[i++] = !injured ? "ok" : DNF ? "DNF" : "injured";
        // itemValues[i++] = gr.tvarsa.hrace.utility.UtString.roundStr( DSQ,2);
        itemValues[i++] = DSQ ? "DSQ" : "qual";
        for (int j = 0; j < itemLines.length; j++)
        {
            y += lineHeight;
            // g2d.drawString(itemLines[j], x+5, y-3);
            g2d.drawString(itemValues[j], x + 5 + 0 * widthSplit, y - 3);
        }
    }

    public double getSpeedAverage()
    {
        double total = 0;
        for (int i = 0; i < speeds.length; i++)
            total += speeds[i];
        return total * 0.5 / speeds.length;
    }

}

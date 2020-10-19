package gr.tvarsa.hrace.dto;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.gui.CameraPanel;
import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.model.Horse;

public class HistoryStep
{
    private double time;
    private String speed;
    private String order;
    private double xPos[] = new double[Engine.MAX_RACE_HORSES];
    /** How much up or down in horse heights is the horse from its expected y position (1=a full horse height). */
    private double yPosHeightDrift[] = new double[Engine.MAX_RACE_HORSES];
    private int horseImages[] = new int[Engine.MAX_RACE_HORSES];

    public int[] getHorseImages()
    {
        return horseImages;
    }

    public int getHorseImage(int i)
    {
        if (i < 0 || i >= horseImages.length) return -1;
        return horseImages[i];
    }

    public void getHorseImage(Horse horse, int imageIndex)
    {
        if (imageIndex < 0 || imageIndex >= horseImages.length) return;
        horse.setCurrentImageIndex(horseImages[imageIndex]);
    }

    public void setHorseImage(int horseNo, int imageIndex)
    {
        if (horseNo < 0 || horseNo >= horseImages.length) return;
        this.horseImages[horseNo] = imageIndex;
    }

    public String getOrder()
    {
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }

    public String getSpeed()
    {
        return speed;
    }

    public void setSpeed(String speed)
    {
        this.speed = speed;
    }

    public double getTime()
    {
        return time;
    }

    public void setTime(double time)
    {
        this.time = time;
    }

    public double getXPos(int horseNo)
    {
        if (horseNo < 0 || horseNo >= horseImages.length) return -1;
        return xPos[horseNo];
    }

    public void setXPos(int horseNo, double xPos)
    {
        if (horseNo < 0 || horseNo >= horseImages.length) return;
        this.xPos[horseNo] = xPos;
    }

    /** How much up or down in horse heights is the horse from its expected y position (1=a full horse height). */
    public double getYPosHeightDrift(int horseNo)
    {
        if (horseNo < 0 || horseNo >= horseImages.length) return -1;
        return yPosHeightDrift[horseNo];
    }

    /** How much up or down in horse heights is the horse from its expected y position (1=a full horse height). */
    public void setYPosHeightDrift(int horseNo, double drift)
    {
        if (horseNo < 0 || horseNo >= horseImages.length) return;
        this.yPosHeightDrift[horseNo] = drift;
    }

    public double getFaceX(int horseNo)
    {
        if (horseNo < 0 || horseNo >= horseImages.length) return -1;
        Horse horse = HorjeRace.engine.getHorse(horseNo);
        double x2 = CameraPanel.horseWidth * horse.getImage(horseImages[horseNo]).getX2()
                / horse.getImage(horseImages[horseNo]).getWidth();
        return xPos[horseNo] + x2;
    }
}

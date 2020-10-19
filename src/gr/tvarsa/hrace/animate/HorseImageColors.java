package gr.tvarsa.hrace.animate;

import gr.tvarsa.hrace.gui.CameraPanel;
import gr.tvarsa.hrace.utility.UtMath;

import java.awt.Color;

public class HorseImageColors
{
    private Color horseBodyColor = null;
    private Color jockeySkinColor = null;
    private Color reinColor = null;
    private Color jockeyPantsColor = null;
    private Color jockeyShirtColor = null;
    private Color hooveColor = null;
    private Color horseTailColor = null;

    private static Color rangeColor(int r1, int g1, int b1, int r2, int g2, int b2)
    {
        double at = UtMath.rnd(0.0, 1.0);
        int r = (int)(r1 + (r2 - r1) * at);
        int g = (int)(g1 + (g2 - g1) * at);
        int b = (int)(b1 + (b2 - b1) * at);
        return new Color(r, g, b);
    }

    private static Color rndColor(int r1, int g1, int b1, int r2, int g2, int b2)
    {
        int r = (int)(r1 + (r2 - r1) * UtMath.rnd(0.0, 1.0));
        int g = (int)(g1 + (g2 - g1) * UtMath.rnd(0.0, 1.0));
        int b = (int)(b1 + (b2 - b1) * UtMath.rnd(0.0, 1.0));
        return new Color(r, g, b);
    }

    public HorseImageColors()
    {
        //
        int type = UtMath.rnd(1, 100);
        boolean isWhite = type < 7;
        boolean isBlack = !isWhite && type < 22;
        boolean isBrown = !(isBlack || isWhite);
        horseBodyColor = //
                isBlack ? rangeColor(0, 0, 0, 10, 10, 10) : //
                        isWhite ? rangeColor(240, 240, 240, 255, 255, 255) : //
                                rangeColor(0, 0, 0, 230, 150, 0);
        horseTailColor = //
                isBrown ? rangeColor(0, 0, 0, horseBodyColor.getRed(), horseBodyColor.getGreen(), horseBodyColor.getBlue()) : //
                        isWhite ? rangeColor(128, 128, 128, horseBodyColor.getRed(), horseBodyColor.getGreen(),
                                horseBodyColor.getBlue()) : //
                                horseBodyColor;
        //
        jockeySkinColor = rangeColor(204, 144, 0, 255, 235, 252);
        reinColor = isDarker(horseBodyColor, 100) ? rangeColor(164, 164, 164, 222, 222, 222) : rangeColor(0, 0, 0, 64, 64, 64);
        jockeyPantsColor = isWhite ? rangeColor(0, 0, 0, 90, 90, 90) : rangeColor(250, 250, 250, 255, 255, 255);
        do
        {
            jockeyShirtColor = rndColor(0, 0, 0, 255, 255, 255);
        }
        while (isGray(jockeyShirtColor) || isSimilar(jockeyShirtColor, CameraPanel.FIELD_COLOR, 15));
        hooveColor = rangeColor(0, 0, 0, 60, 30, 0);
    }

    private boolean isGray(Color c)
    {
        int rg = Math.abs(c.getRed() - c.getGreen());
        int gb = Math.abs(c.getGreen() - c.getBlue());
        int rb = Math.abs(c.getRed() - c.getBlue());
        int min = 160;
        int count = 0;
        count += rg > min ? 1 : 0;
        count += gb > min ? 1 : 0;
        count += rb > min ? 1 : 0;
        return count <= 1;
    }

    private boolean isDarker(Color c, int level)
    {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int count = 0;
        count += r <= level ? 1 : 0;
        count += g <= level ? 1 : 0;
        count += b <= level ? 1 : 0;
        return count >= 3;
    }

    private boolean isSimilar(Color c, Color d, int level)
    {
        int r = Math.abs(c.getRed() - d.getRed());
        int g = Math.abs(c.getGreen() - d.getGreen());
        int b = Math.abs(c.getBlue() - d.getBlue());
        int count = 0;
        count += r <= level ? 1 : 0;
        count += g <= level ? 1 : 0;
        count += b <= level ? 1 : 0;
        return count >= 3;
    }

    public Color getHorseBodyColor()
    {
        return horseBodyColor;
    }

    public Color getJockeySkinColor()
    {
        return jockeySkinColor;
    }

    public Color getReinColor()
    {
        return reinColor;
    }

    public Color getJockeyPantsColor()
    {
        return jockeyPantsColor;
    }

    public Color getJockeyShirtColor()
    {
        return jockeyShirtColor;
    }

    public Color getHooveColor()
    {
        return hooveColor;
    }

    public Color getHorseTailColor()
    {
        return horseTailColor;
    }

}

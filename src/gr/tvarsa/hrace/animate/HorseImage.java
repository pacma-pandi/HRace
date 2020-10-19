package gr.tvarsa.hrace.animate;

import gr.tvarsa.hrace.animate.HorseImageColors;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class HorseImage
{
    private static final int transparentRGB = 0; // this is NOT black!
    //
    private static final int backgroundTemplateRGB = new Color(255, 255, 255).getRGB();
    private static final int horseBodyTemplateRGB = new Color(0, 0, 0).getRGB();
    private static final int jockeySkinTemplateRGB = new Color(255, 0, 255).getRGB();
    private static final int reinTemplateRGB = new Color(255, 255, 0).getRGB();
    private static final int jockeyPantsTemplateRGB = new Color(255, 0, 0).getRGB();
    private static final int jockeyShirtTemplateRGB = new Color(0, 0, 255).getRGB();
    private static final int hooveTemplateRGB = new Color(0, 255, 255).getRGB();
    private static final int horseTailTemplateRGB = new Color(0, 255, 0).getRGB();

    private BufferedImage graphics;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int width;
    private int height;
    private HorseImageColors horseImageColors;

    public HorseImage(Image image, HorseImageColors horseImageColors)
    {
        x1 = -1;
        x2 = -1;
        y1 = -1;
        y2 = -1;
        if (image == null) return;
        this.horseImageColors = horseImageColors;
        width = image.getWidth(null);
        height = image.getHeight(null);
        graphics = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics.getGraphics().drawImage(image, 0, 0, null);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
            {
                int pixelRGB = graphics.getRGB(x, y);
                if (pixelRGB != backgroundTemplateRGB)
                {
                    if (x1 == -1 || x < x1) x1 = x;
                    if (x2 == -1 || x > x2) x2 = x;
                    if (y1 == -1 || y < y1) y1 = y;
                    if (y2 == -1 || y > y2) y2 = y;
                }
                int rgb = transparentRGB;
                if (pixelRGB == backgroundTemplateRGB) //
                    rgb = transparentRGB;
                else if (pixelRGB == horseBodyTemplateRGB) //
                    rgb = horseImageColors.getHorseBodyColor().getRGB();
                else if (pixelRGB == jockeySkinTemplateRGB) //
                    rgb = horseImageColors.getJockeySkinColor().getRGB();
                else if (pixelRGB == reinTemplateRGB) //
                    rgb = horseImageColors.getReinColor().getRGB();
                else if (pixelRGB == jockeyPantsTemplateRGB) //
                    rgb = horseImageColors.getJockeyPantsColor().getRGB();
                else if (pixelRGB == jockeyShirtTemplateRGB) //
                    rgb = horseImageColors.getJockeyShirtColor().getRGB();
                else if (pixelRGB == hooveTemplateRGB) //
                    rgb = horseImageColors.getHooveColor().getRGB();
                else if (pixelRGB == horseTailTemplateRGB) //
                    rgb = horseImageColors.getHorseTailColor().getRGB();
                graphics.setRGB(x, y, rgb);
            }
    }

    public BufferedImage getGraphics()
    {
        return graphics;
    }

    public int getX1()
    {
        return x1;
    }

    public int getX2()
    {
        return x2;
    }

    public int getY1()
    {
        return y1;
    }

    public int getY2()
    {
        return y2;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}

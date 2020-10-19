package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.model.Horse;
import gr.tvarsa.hrace.utility.UtGui;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JComponent;

public class CameraPanel extends JComponent
{
    public static final Color FIELD_COLOR = new Color(0, 192, 0);
    private static int red1 = 33, red2 = 21, green1 = 164, green2 = 244, blue1 = 116, blue2 = 70;

    private Image screenBuffer;
    private double cameraX;
    private boolean flashReplay = false;

    // draw variables
    private static double pixelsPerMeter = 1.0; // default value, will change
    public static final double horseWidth = 3.0;

    public static double toPixels(double meters)
    {
        return meters * pixelsPerMeter;
    }

    public static double toMeters(double pixels)
    {
        return pixels / pixelsPerMeter;
    }

    public static void randomHorseColors()
    {
        red1 = UtMath.rnd(0, 255);
        red2 = UtMath.rnd(0, 255);
        green1 = UtMath.rnd(0, 255);
        green2 = UtMath.rnd(0, 255);
        blue1 = UtMath.rnd(0, 255);
        blue2 = UtMath.rnd(0, 255);
        HorjeRace.engine.writeln("\ngr.tvarsa.hrace.model.Horse position colors:   red1=" + red1 + "   red2=" + red2 + "   green1=" + green1
                + "   green2=" + green2 + "   blue1=" + blue1 + "   blue2=" + blue2);
    }

    public static Color horseColor(int i)
    {
        return new Color((red1 * i + red2) % 255, (green1 * i + green2) % 255, (blue1 * i + blue2) % 255);
    }

    public void setCameraX(double cameraX)
    {
        this.cameraX = cameraX;
    }

    public void addCameraX(double x)
    {
        this.cameraX += x;
    }

    public double getCameraX()
    {
        return cameraX;
    }

    public double getTrackLengthMeters()
    {
        return toMeters(this.getWidth());
    }

    public void setFlashReplay(boolean flashReplay)
    {
        this.flashReplay = flashReplay;
    }

    public void toggleFlashReplay()
    {
        flashReplay = !flashReplay;
    }

    private void checkScreenBuffer()
    {
        if (screenBuffer != null && screenBuffer.getWidth(null) == this.getWidth()
                && screenBuffer.getHeight(null) == this.getHeight())
            return;
        screenBuffer = createImage(this.getWidth(), this.getHeight());
        pixelsPerMeter = screenBuffer.getHeight(null) / 16.48;
        // gr.tvarsa.hrace.gui.HorjeDisplay display = gr.tvarsa.hrace.app.HorjeRace.display;
        // if (display == null) return;
        // int refreshMode = display.getInfoPanel().getRefreshMode();
        // if (refreshMode != gr.tvarsa.hrace.gui.InfoPanel.FULL_REFRESH)
        // {
        // display.getInfoPanel().setRefreshMode(gr.tvarsa.hrace.gui.InfoPanel.FULL_REFRESH);
        // display.getInfoPanel().repaint();
        // display.getInfoPanel().setRefreshMode(refreshMode);
        // }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        updateScreenContent(g);
    }

    public void updateScreenContent(Graphics g)
    {
        checkScreenBuffer();
        HorjeDisplay display = HorjeRace.display;
        Engine engine = HorjeRace.engine;
        Graphics2D g2d = (Graphics2D)screenBuffer.getGraphics();
        g2d.setColor(FIELD_COLOR);
        g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        if (engine == null) return;
        // convenience variables
        double panelWidth = screenBuffer.getWidth(null);
        double panelHeight = screenBuffer.getHeight(null);
        if (engine.getStage().compareTo(Engine.Stage.SHOWING_RACE_INFO) < 0)
        {
            UtGui.drawStringBoxed(g2d, "Preparing horses...", true, (int)panelWidth, (int)panelHeight, 0, 0,
                    new Font("Arial", Font.BOLD, 24), new Color(0, 150, 0), Color.white, new Color(0, 110, 0));
            g2d.dispose();
            g.drawImage(screenBuffer, 0, 0, null);
            return;
        }
        double horseGapY = 0.2;
        int horseImageWidth = Horse.getImageWidth();
        int horseImageHeight = Horse.getImageHeight();
        double horseHeight = 1.75;
        int horses = engine.countHorses();
        double railGapX = 1.0;
        double topRailY = 1.0;
        double horseRailGapY = 0.4;
        double railHeight = 0.7;
        double bottomRailY = topRailY + horseRailGapY + (horses - 1) * (horseHeight + horseGapY) + horseRailGapY - 0.08;
        double railX = -cameraX % railGapX;
        //
        g2d.setColor(Color.yellow);
        g2d.draw(new Line2D.Double(0, toPixels(topRailY), panelWidth, toPixels(topRailY)));
        for (double i = railX; i <= toMeters(panelWidth); i += railGapX)
            g2d.draw(new Line2D.Double(toPixels(i), toPixels(topRailY), toPixels(i), toPixels(topRailY + railHeight)));
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.BOLD, (int)toPixels(0.75)));
        for (int i = 0; i <= engine.getRaceFurlongs(); i++)
        {
            String s = "" + i;
            if (i == 0) s = "F";
            float toPixels = (float)toPixels(Engine.furlongsToLength(engine.getRaceFurlongs() - i) - cameraX);
            g2d.drawString(s, toPixels, (float)toPixels(topRailY) - 1);
        }
        g2d.setColor(new Color(128, 255, 128));
        double tmpX = toPixels(-cameraX);
        float tmpY = (float)toPixels(0.2);
        int historyStep = engine.getHistoryStep();
        if (historyStep < 0 && engine.getSimulationTime() > 0
                || historyStep >= 0 && engine.getHistoryStep(historyStep).getTime() > 0)
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1, new float[] {tmpY, tmpY}, 0));
        g2d.draw(new Line2D.Double(tmpX, toPixels(topRailY + railHeight), tmpX, toPixels(bottomRailY)));
        tmpX = toPixels(Engine.furlongsToLength(engine.getRaceFurlongs()) - cameraX);
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new Line2D.Double(tmpX, toPixels(topRailY + railHeight), tmpX, toPixels(bottomRailY)));
        for (int i = 0; i < horses; i++)
        {
            Horse horse = engine.getHorse(i);
            if (horse == null || horse.getCurrentImage() == null) continue; // has not been setup yet
            double driftY = horseHeight * horse.getYPosHeightDrift();
            double y = i * (horseHeight + horseGapY) + railHeight + horseRailGapY + topRailY + driftY;
            y = UtMath.ranged(y, topRailY + railHeight, bottomRailY + railHeight);
            g2d.drawImage(horse.getCurrentImage().getGraphics(), (int)toPixels(horse.getXPos() - cameraX),
                    (int)toPixels(y - horseHeight), (int)toPixels(horseWidth + horse.getXPos() - cameraX), (int)toPixels(y), 0,
                    0, horseImageWidth, horseImageHeight, null);
            if (HorjeRace.display.getShowHorseRTInfoCheck().isSelected())
                horse.paintInfoAt(g2d, (int)toPixels(horse.getXPos() - cameraX) - 90,
                        (int)toPixels(y - horseHeight + horseHeight / 3), 70, (int)toPixels(horseHeight) / 2);
        }
        g2d.setColor(Color.yellow);
        g2d.draw(new Line2D.Double(0, toPixels(bottomRailY), panelWidth, toPixels(bottomRailY)));
        for (double i = railX; i <= toMeters(panelWidth); i += railGapX)
            g2d.draw(new Line2D.Double(toPixels(i), toPixels(bottomRailY), toPixels(i), toPixels(bottomRailY + railHeight)));
        //
        if (historyStep >= 0)
        {
            if (flashReplay) UtGui.drawStringBoxed(g2d, "R", false, 0, 0, 10, 10, new Font("Arial", Font.BOLD, 16), Color.red,
                    Color.white, Color.white);
            boolean all = HorjeRace.replayControl.getMarkAllHorsesCheck().isSelected();
            boolean first = HorjeRace.replayControl.getMarkFirstHorseCheck().isSelected();
            boolean next = HorjeRace.replayControl.getMarkNextToFinishCheck().isSelected();
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            if (all)
            {
                for (int i = 0; i < engine.countHorses(); i++)
                {
                    Horse horse = engine.getHorse(i);
                    g2d.setColor(horseColor(horse.getNumber()));
                    int at = (int)toPixels(horse.getFaceX() - cameraX);
                    g2d.drawLine(at, 0, at, (int)panelHeight);
                    g2d.drawString("gr.tvarsa.hrace.model.Horse " + horse.getNumber(), 10, i * 16 + 60);
                }
            }
            else
            {
                Horse firstHorse = null;
                if (first)
                {
                    firstHorse = engine.getHistoryFirstHorse();
                    g2d.setColor(horseColor(firstHorse.getNumber()));
                    int at = (int)toPixels(firstHorse.getFaceX() - cameraX);
                    g2d.drawLine(at, 0, at, (int)panelHeight);
                    g2d.drawString("gr.tvarsa.hrace.model.Horse " + firstHorse.getNumber(), 10, 60);
                }
                if (next)
                {
                    Horse horse = engine.getHistoryNextToFinishHorse();
                    if (horse != null && horse != firstHorse)
                    {
                        g2d.setColor(horseColor(horse.getNumber()));
                        int at = (int)toPixels(horse.getFaceX() - cameraX);
                        g2d.drawLine(at, 0, at, (int)panelHeight);
                        g2d.drawString("gr.tvarsa.hrace.model.Horse " + horse.getNumber(), 10, 60 + (first ? 16 : 0));
                    }
                }
            }
        }
        //
        Point p = display.getMousePressedAt(true);
        if (p.x >= 0)
        {
            g2d.setColor(Color.cyan);
            g2d.draw(new Line2D.Double(p.x, 0, p.x, panelHeight));
            String before = "";
            String at = "";
            String after = "";
            List<Horse> orderedHorses = engine.getOrderedHorses();
            for (int i = horses - 1; i >= 0; i--)
            {
                Horse horse = orderedHorses.get(i);
                int no = horse.getNumber();
                if (Math.abs(toPixels(horse.getFaceX() - cameraX) - p.x) < 0.5)
                    at += no + " ";
                else if (toPixels(horse.getFaceX() - cameraX) > p.x)
                    after += no + " ";
                else
                    before += no + " ";
            }
            String s = "";
            if (before.length() > 0) s = "Before : " + before;
            if (at.length() > 0) s = s.trim() + "   At : " + at;
            if (after.length() > 0) s = s.trim() + "   After : " + after;
            s = UtString.copyBefore(display.getStatus(), "[", true).trim() + Engine.GAP + "[" + s.trim() + "]";
            if (!display.getStatus().equals(s)) display.setStatus(s);
        }
        g2d.dispose();
        g.drawImage(screenBuffer, 0, 0, null);
    }
}

package gr.tvarsa.hrace.utility;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

public class UtGui
{
    private static int keyCode = -1;

    private static Dimension processStringBoxed(Graphics2D g2d, String text, boolean centered, int centerWidth,
            int centerHeight, int shiftX, int shiftY, Font font, Color boxColor, Color textColor, Color borderColor,
            boolean show)
    {
        g2d.setFont(font);
        FontRenderContext frc = g2d.getFontRenderContext();
        int lineGap = 0;
        String texts[] = text.split("\n");
        // texts = new String[] {texts.length > 1 ? texts[1] : texts[0]};
        LineMetrics metrics = font.getLineMetrics(texts[0], frc);
        float ascent = metrics.getAscent(); // Top of text to baseline
        float textWidth = 0;
        for (int i = 0; i < texts.length; i++)
        {
            Rectangle2D bounds = font.getStringBounds(texts[i], frc);
            textWidth = Math.max(textWidth, (float)bounds.getWidth());
        }
        float textHeight = metrics.getHeight() * texts.length + lineGap * (texts.length - 1);
        int boxWidth = (int)(textWidth + 10);
        int boxHeight = (int)(textHeight + 10);
        if (!show) return new Dimension(boxWidth, boxHeight);
        int centerX = 0;
        int centerY = 0;
        if (centered)
        {
            centerX = (centerWidth - boxWidth) / 2;
            centerY = (centerHeight - boxHeight) / 2;
        }
        Rectangle box = new Rectangle(centerX + shiftX, centerY + shiftY, boxWidth, boxHeight);
        float x0 = (float)(box.getX() + (boxWidth - textWidth) / 2);
        float y0 = (float)(box.getY() + (boxHeight - textHeight) / 2 + ascent);
        if (boxColor != null)
        {
            g2d.setColor(boxColor);
            g2d.fill(box);
        }
        if (borderColor != null)
        {
            g2d.setColor(borderColor);
            g2d.draw(box);
        }
        if (textColor != null) g2d.setColor(textColor);
        for (int i = 0; i < texts.length; i++)
        {
            g2d.drawString(texts[i], x0, y0);
            y0 += metrics.getHeight() + lineGap;
        }
        return new Dimension(boxWidth, boxHeight);
    }

    public static Dimension drawStringBoxed(Graphics2D g2d, String text, boolean centered, int centerWidth, int centerHeight,
            int shiftX, int shiftY, Font font, Color boxColor, Color textColor, Color borderColor)
    {
        return processStringBoxed(g2d, text, centered, centerWidth, centerHeight, shiftX, shiftY, font, boxColor, textColor,
                borderColor, true);
    }

    /** Get the screen coordinates available to the user (ie where there is no overlap with the taskbar) */
    public static Insets getScreenBounds()
    {
        Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        return new Insets(r.y, r.x, r.y + r.height - 1, r.x + r.width - 1);
    }

    public static int waitForKeys(JFrame frame)
    {
        return waitForKeys(frame, null);
    }

    public static int waitForKeys(JFrame frame, int... keys)
    {
        KeyAdapter keyAdapter = new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                keyCode = e.getKeyCode();
            }
        };
        frame.addKeyListener(keyAdapter);
        keyCode = 0;
        while (true)
        {
            UtCpu.pause(50);
            if ((keys == null || keys.length == 0) && keyCode > 0)
            {
                frame.removeKeyListener(keyAdapter);
                return keyCode;
            }
            if (keys == null) continue;
            for (int i = 0; i < keys.length; i++)
                if (keyCode == keys[i])
                {
                    frame.removeKeyListener(keyAdapter);
                    return keyCode;
                }
        }
    }

    public static char waitForChars(JFrame frame)
    {
        return waitForChars(frame, false, null);
    }

    public static char waitForChars(JFrame frame, String keys)
    {
        return waitForChars(frame, false, keys, null);
    }

    public static char waitForChars(JFrame frame, String keys, int... specialChars)
    {
        return waitForChars(frame, false, keys, specialChars);
    }

    public static char waitForChars(JFrame frame, final boolean allLowerCase, String keys, int... specialChars)
    {
        KeyAdapter keyAdapter = new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                keyCode = e.getKeyChar();
                if (allLowerCase) keyCode = Character.toLowerCase(keyCode);
            }
        };
        frame.addKeyListener(keyAdapter);
        if (allLowerCase) keys = keys.toLowerCase();
        keyCode = 0;
        while (true)
        {
            UtCpu.pause(50);
            if ((keys == null || keys.length() == 0) && keyCode > 0)
            {
                frame.removeKeyListener(keyAdapter);
                return (char)keyCode;
            }
            if (keys == null) continue;
            if (keys.indexOf((char)keyCode) > -1)
            {
                frame.removeKeyListener(keyAdapter);
                return (char)keyCode;
            }
            if (specialChars == null || specialChars.length == 0) continue;
            for (int i = 0; i < specialChars.length; i++)
                if (keyCode == specialChars[i])
                {
                    frame.removeKeyListener(keyAdapter);
                    return (char)keyCode;
                }
        }
    }

    public static void centerOnComponent(Component back, Window front)
    {
        int backWidth, backHeight;
        int x, y;
        int extendedState = 0;
        JFrame frameFront = null;
        if (front instanceof JFrame) frameFront = (JFrame)front;
        if (frameFront != null) frameFront.getExtendedState();
        if (frameFront != null && front.isVisible() && extendedState != Frame.NORMAL)
            frameFront.setExtendedState(Frame.NORMAL);
        if (back == null)
        {
            x = 0;
            y = 0;
            backWidth = getScreenWidth();
            backHeight = getScreenHeight();
        }
        else
        {
            x = back.getX();
            y = back.getY();
            backWidth = back.getWidth();
            backHeight = back.getHeight();
        }
        x += (backWidth - front.getWidth()) / 2;
        y += (backHeight - front.getHeight()) / 2;
        // if (!allowNonVisibleWindowTop)
        // {
        // x = Math.max(0, x);
        // x = Math.min(x, getScreenWidth() - 30);
        // y = Math.max(0, y);
        // y = Math.min(y, getScreenHeight() - 30);
        // }
        front.setLocation(x, y);
        if (frameFront != null && front.isVisible()) frameFront.setExtendedState(extendedState);
    }

    /**
     * Gets the screen dimensions excluding any taskbar
     */
    public static Dimension getScreenDimensions()
    {
        return getScreenDimensions(false);
    }

    /**
     * Gets the screen width excluding any taskbar element at the left or right hand side of the screen
     */
    public static int getScreenWidth()
    {
        return getScreenWidth(false);
    }

    /**
     * Gets the screen height excluding any taskbar element at the top or bottom of the screen
     */
    public static int getScreenHeight()
    {
        return getScreenHeight(false);
    }

    public static Dimension getScreenDimensions(boolean includeTaskbar)
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        if (includeTaskbar)
        {
            Insets i = getScreenTaskbarInsets();
            d.width -= i.left + i.right;
            d.height -= i.top + i.bottom;
        }
        return d;
    }

    public static int getScreenWidth(boolean includeTaskbar)
    {
        return getScreenDimensions(includeTaskbar).width;
    }

    public static int getScreenHeight(boolean includeTaskbar)
    {
        return getScreenDimensions(includeTaskbar).height;
    }

    public static Insets getScreenTaskbarInsets()
    {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                .getDefaultConfiguration();
        return Toolkit.getDefaultToolkit().getScreenInsets(gc);
    }
    public static synchronized void refresh(final JComponent component)
    {
        // Refresher.refresh(component);
        component.paintImmediately(0, 0, component.getWidth(), component.getHeight());
    }

    public static synchronized void refresh(final Window window)
    {
        if (window instanceof JFrame)
        {
            refresh(((JFrame)window).getRootPane());
            JMenuBar menu = ((JFrame)window).getJMenuBar();
            if (menu != null) refresh(menu);
        }
        if (window instanceof JDialog) refresh(((JDialog)window).getRootPane());
    }
    public static void showDialog(Component parent, String title, String message, int messageType, Icon... icon)
    {
        if (icon != null && icon.length > 0 && icon[0] != null)
            JOptionPane.showMessageDialog(parent, message + "\n", title, messageType, icon[0]);
        else
            JOptionPane.showMessageDialog(parent, message + "\n", title, messageType);
    }

}

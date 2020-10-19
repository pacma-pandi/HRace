package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.dto.BetResult;
import gr.tvarsa.hrace.model.Bet;
import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.model.Horse;
import gr.tvarsa.hrace.model.Player;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

public class InfoPanel extends JComponent
{
    public static final int NO_REFRESH = 0;
    public static final int FULL_REFRESH = 1;
    //
    private static Color colors[];
    private static final int FIELD_COLOR = 0;
    private static final int HORSE_TABLE_COLOR = 1;
    private static final int DNF_COLOR = 2;
    private static final int DSQ_COLOR = 3;
    private static final int RECORD_TIME_COLOR = 4;
    private static final int RECORD2_TIME_COLOR = 5;
    private static final int PAYOFF_TABLE_COLOR = 6;
    private static final int TEXT_COLOR = 7;
    private static final int HIGHLIGHT_TEXT_COLOR = 8;
    private static final int BLACK_COLOR = 9; // must be last
    //
    private static final int LEFT_J = UtString.JUSTIFY_LEFT + 1;
    private static final int RIGHT_J = UtString.JUSTIFY_RIGHT + 1;
    //
    private static final Font signLabelFont = new Font("Arial", Font.PLAIN, 10);
    private static final Font smallFont = new Font("Arial", Font.PLAIN, 6);
    private static final Font headerFont = new Font("Courier New", Font.BOLD, 13);
    private static final Font dataFont = new Font("Courier New", Font.PLAIN, 13);
    private static final Font dataFont2 = new Font("Courier New", Font.PLAIN, 12);
    //
    private static final int TABLE_LINES = 8;
    private static final int TABLE_GAP_X = 5;
    private static String[] horseHeaders = {"H", "PERF", "LV", "PAY ", "TIME   "};
    private static String[] playerHeaders = {"NAME    ", "MONEY  ", "TYPE  ", "HORSE", "BET    ", "PAY ", "RET", "WON    "};
    private static String[] payoffHeaders = {"TYPE  ", "HORSE", "PAY  "};

    private Image screenBuffer;
    private int refreshMode = FULL_REFRESH;
    private double bestTime;
    private Player highlightedPlayer;

    public static String doubleToTime(double seconds)
    {
        int s = (int)seconds;
        int s100 = (int)((seconds - s + 0.005) * 100);
        if (s100 == 100)
        {
            s++;
            s100 = 0;
        }
        int m = s / 60;
        s = s - m * 60;
        String text;
        if (s100 < 10)
            text = ".0" + s100;
        else
            text = "." + s100;
        if (s < 10)
            text = " " + s + text;
        else
            text = s + text;
        if (m > 0)
            text = m + ":" + text;
        else
            text = "  " + text;
        return text;
    }

    public InfoPanel()
    {
        colors = new Color[BLACK_COLOR + 1];
        colors[FIELD_COLOR] = new Color(128, 128, 255);
        colors[HORSE_TABLE_COLOR] = new Color(220, 220, 220);
        colors[PAYOFF_TABLE_COLOR] = colors[HORSE_TABLE_COLOR];
        colors[DNF_COLOR] = new Color(80, 90, 100);
        colors[DSQ_COLOR] = new Color(100, 90, 80);
        colors[RECORD_TIME_COLOR] = Color.red;
        colors[RECORD2_TIME_COLOR] = new Color(192, 0, 0);
        colors[TEXT_COLOR] = new Color(0, 0, 128);
        colors[HIGHLIGHT_TEXT_COLOR] = new Color(255, 0, 0);
        colors[BLACK_COLOR] = Color.black;
    }

    private void checkScreenBuffer()
    {
        if (screenBuffer != null && screenBuffer.getWidth(null) == this.getWidth()
                && screenBuffer.getHeight(null) == this.getHeight())
            return;
        screenBuffer = createImage(this.getWidth(), this.getHeight());
    }

    public void setBestTime(int bestTimeSec)
    {
        this.bestTime = bestTimeSec / 1000.0;
    }

    public void setBestTime(double bestTimeMilli)
    {
        this.bestTime = bestTimeMilli;
    }

    public int getRefreshMode()
    {
        return refreshMode;
    }

    public void setRefreshMode(int refreshMode)
    {
        this.refreshMode = refreshMode;
    }

    public Player getHighlightedPlayer()
    {
        return highlightedPlayer;
    }

    public void setHighlightedPlayer(Player highlightedPlayer)
    {
        this.highlightedPlayer = highlightedPlayer;
    }

    private int drawTable(String[] headers, String[][] data, int x, int y, int lines, Color backColor)
    {
        Graphics2D g2d = (Graphics2D)screenBuffer.getGraphics();
        int borderX = 5;
        int width = 2;
        lines++;
        for (int i = 0; i < headers.length; i++)
            width += borderX + borderX + headers[i].length() * 8;
        int height = lines * 17 + 1;
        g2d.setColor(backColor);
        Rectangle rect = new Rectangle(x, y, width, height);
        g2d.fill(rect);
        g2d.setColor(colors[BLACK_COLOR]);
        g2d.draw(rect);
        int atX = x;
        for (int x2 = 0; x2 < headers.length; x2++)
        {
            int atY = y;
            for (int y2 = -1; y2 < lines; y2++)
            {
                String s = "" + BLACK_COLOR + RIGHT_J;
                if (y2 == -1)
                {
                    g2d.setFont(headerFont);
                    s = "" + BLACK_COLOR + LEFT_J + headers[x2];
                }
                else
                {
                    g2d.setFont(dataFont);
                    if (x2 < data.length && y2 < data[x2].length && data[x2][y2] != null) s = data[x2][y2];
                }
                int color = s.charAt(0) - '0';
                int justify = s.charAt(1) - '0' - 1;
                s = s.substring(2);
                if (UtMath.inRange(color, 0, colors.length))
                    g2d.setColor(colors[color]);
                else if (HorjeRace.display.getDebugToConsole().isSelected())
                    System.out.println("\nWrong color index " + color + " for string '" + s + "'");
                s = UtString.justifyString(s, headers[x2].length(), justify);
                g2d.drawString(s, atX + borderX + 1, atY + 14);
                if (y2 < lines - 2)
                {
                    atY += 17;
                    g2d.setColor(colors[BLACK_COLOR]);
                    if (x2 == 0) g2d.draw(new Line2D.Double(x, atY, x + width, atY));
                }
            }
            if (x2 == headers.length - 1) break;
            atX += headers[x2].length() * 8 + borderX + borderX;
            g2d.setColor(colors[BLACK_COLOR]);
            g2d.draw(new Line2D.Double(atX, y, atX, y + height));
        }
        return width;
    }

    private void showSign(Graphics2D g2d, Color backColor, Color fontColor, int signWidth, int signHeight, int signX, int signY,
            String label, String data, int dataX, Font textFont)
    {
        if (data.length() > 15) data = data.substring(0, 15);
        g2d.setColor(backColor);
        g2d.fill(new Rectangle(signX, signY, signWidth, signHeight));
        g2d.setFont(signLabelFont);
        g2d.setColor(Color.black);
        g2d.drawString(label, signX - 36, signY + signHeight - 5);
        g2d.setFont(textFont);
        g2d.setColor(fontColor);
        g2d.drawString(data, signX + dataX, signY + signHeight - 5);
    }

    private void showSign(Graphics2D g2d, Color backColor, Color fontColor, int signWidth, int signHeight, int signX, int signY,
            String label, String data, int dataX)
    {
        showSign(g2d, backColor, fontColor, signWidth, signHeight, signX, signY, label, data, dataX, headerFont);
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
        if (refreshMode == NO_REFRESH) return;
        int panelWidth;
        int panelHeight;
        checkScreenBuffer();
        Graphics2D g2d = (Graphics2D)screenBuffer.getGraphics();
        panelWidth = screenBuffer.getWidth(null);
        panelHeight = screenBuffer.getHeight(null);
        g2d.setColor(colors[FIELD_COLOR]);
        g2d.fill(new Rectangle2D.Double(0, 0, panelWidth, panelHeight));
        Engine engine = HorjeRace.engine;
        if (engine == null) return;
        BetResult betResult = HorjeRace.betResult;
        int noOfHorses = engine.countHorses();
        // timer
        int signWidth = 77;
        int signHeight = 18;
        int signX = panelWidth - signWidth - 6;
        int signY = panelHeight - signHeight - 7;
        String text;
        int historyStep = engine.getHistoryStep();
        if (historyStep < 0)
            text = engine.getHorseOrder(false);
        else
            text = engine.getHistoryStep(historyStep).getOrder();
        showSign(g2d, Color.white, Color.black, signWidth, signHeight, signX, signY, "Order", text, 4, signLabelFont);
        signY -= signHeight + 3;
        if (historyStep < 0)
            text = engine.getMaxSpeedStr(false);
        else
            text = engine.getHistoryStep(historyStep).getSpeed();
        showSign(g2d, new Color(255, 255, 150), Color.black, signWidth, signHeight, signX, signY, "Speed", text, 3, dataFont2);
        signY -= signHeight + 3;
        showSign(g2d, new Color(255, 255, 60), Color.black, signWidth, signHeight, signX, signY, "Fastest",
                engine.getBestSpeedStr(), 3, dataFont2);
        signY -= signHeight + 3;
        if (historyStep < 0)
            text = " " + doubleToTime(engine.getSimulationTime() / 1000.0);
        else
            text = " " + doubleToTime(engine.getHistoryStep(historyStep).getTime());
        showSign(g2d, new Color(150, 255, 150), Color.black, signWidth, signHeight, signX, signY, "Time", text, 9);
        signY -= signHeight + 3;
        showSign(g2d, new Color(80, 240, 80), Color.black, signWidth, signHeight, signX, signY, "Best",
                " " + doubleToTime(bestTime), 9);
        signY -= signHeight + 3;
        showSign(g2d, new Color(170, 0, 0), Color.white, signWidth, signHeight, signX, signY, "Race",
                UtString.justifyRight(engine.getRaceClassification(), 8), 9);
        //
        String[][] data;
        int posX = 5;
        int posY = 5;
        // horses table
        data = new String[horseHeaders.length][noOfHorses];
        boolean finalResults = engine.getStage() == Engine.Stage.FINAL_RESULTS_AVAILABLE;
        if (engine.getStage().compareTo(Engine.Stage.SHOWING_RACE_INFO) >= 0) for (int y = 0; y < noOfHorses; y++)
        {
            Horse horse = engine.getHorse(y);
            int line = y;
            if (finalResults) line = horse.getFinishOrder() - 1;
            if (line < 0 || horse == null) continue;
            data[0][line] = "" + TEXT_COLOR + RIGHT_J + horse.getNumber();
            data[1][line] = "" + TEXT_COLOR + RIGHT_J + UtString.roundStr(horse.getPerformance(), 1);
            data[2][line] = "" + TEXT_COLOR + RIGHT_J + horse.getLevel();
            data[3][line] = "" + TEXT_COLOR + RIGHT_J + horse.getWinPayoff();
            if (horse.isFinished())
            {
                String s = doubleToTime(horse.getFinishTime());
                if (horse.isDNF())
                {
                    s = "" + DNF_COLOR + RIGHT_J + "DNF";
                    g2d.setColor(colors[DNF_COLOR]);
                }
                else if (horse.isDSQ() && finalResults)
                {
                    s = "" + DSQ_COLOR + RIGHT_J + "DSQ";
                    g2d.setColor(colors[DSQ_COLOR]);
                }

                else if (horse.getFinishTime() < bestTime)
                {
                    if (horse.getFinishOrder() == 1)
                        s = "" + RECORD_TIME_COLOR + RIGHT_J + s;
                    else
                        s = "" + RECORD2_TIME_COLOR + RIGHT_J + s;
                }
                else
                    s = "" + TEXT_COLOR + RIGHT_J + s;
                // if (engine.getSimulationTime() >= 10000) horseHeaders[4] = "TIME___"; else horseHeaders[4] = "TIME_";
                data[4][line] = s;
            }
        }
        posX += drawTable(horseHeaders, data, posX, posY, TABLE_LINES, colors[HORSE_TABLE_COLOR]) + TABLE_GAP_X;
        // payoffs table
        data = new String[payoffHeaders.length][Bet.BET_TYPES.length];
        for (int y = 1; y < Bet.BET_TYPES.length; y++)
        {
            data[0][y - 1] = "" + TEXT_COLOR + LEFT_J + Bet.BET_TYPES[y];
            if (finalResults)
            {
                data[1][y - 1] = "" + TEXT_COLOR + LEFT_J + betResult.getPayoffHorses(y);
                data[2][y - 1] = "" + TEXT_COLOR + RIGHT_J + betResult.getPayoff(y);
            }
        }
        posX += drawTable(payoffHeaders, data, posX, posY, Bet.BET_TYPES.length - 1, colors[PAYOFF_TABLE_COLOR]) + TABLE_GAP_X;
        // players table
        data = new String[playerHeaders.length][engine.countPlayers()];
        for (int i = 0; i < engine.countPlayers(); i++)
        {
            Player player = engine.getPlayer(i);
            int textColor = TEXT_COLOR;
            if (player == highlightedPlayer) textColor = HIGHLIGHT_TEXT_COLOR;
            data[0][i] = "" + textColor + LEFT_J + player.getName();
            int moneyHave = player.getMoney();
            if (player.countBets() > 0)
            {
                Bet bet = player.getBet(0);
                int amountBet;
                String amountWon;
                if (HorjeRace.display.getSubtractMoneyBetCheck().isSelected())
                {
                    amountBet = bet.getAmountBet();
                    amountWon = UtString.commaString(bet.getWon());
                }
                else
                {
                    moneyHave = player.getMoney() + bet.getAmountBet();
                    amountBet = bet.getAmountBet();
                    int diff = bet.getWon() - bet.getAmountBet();
                    String s = "";
                    if (diff > 0) s = "+";
                    amountWon = s + UtString.commaString(diff);
                }
                data[2][i] = "" + textColor + LEFT_J + "-";
                if (bet.getBetType() != Bet.NO_BET)
                {
                    data[2][i] = "" + textColor + LEFT_J + Bet.BET_TYPES[bet.getBetType()];
                    String payoffHorses = betResult.getPayoffHorses(bet);
                    data[3][i] = "" + textColor + LEFT_J + payoffHorses;
                    data[4][i] = "" + textColor + RIGHT_J;
                    if (payoffHorses.length() > 0 && payoffHorses.indexOf('?') == -1)
                        data[4][i] += UtString.commaString(amountBet);
                    if (engine.getStage() != Engine.Stage.FINAL_RESULTS_AVAILABLE)
                    {
                        double payoff = betResult.getPayoff(bet);
                        String s;
                        if (payoffHorses.indexOf('?') != -1)
                            s = "";
                        else if (payoff < 100)
                            s = "" + payoff;
                        else
                            s = "" + (int)(payoff + 0.5);
                        data[5][i] = "" + textColor + RIGHT_J + s;
                        data[6][i] = null;
                        data[7][i] = null;
                    }
                    else
                    {
                        data[5][i] = "" + textColor + RIGHT_J + betResult.getPayoff(bet.getBetType());
                        String ret = "x";
                        int percent = (int)(betResult.getBetReturn(bet) * 100);
                        if (percent > 0) ret = "" + percent;
                        data[6][i] = "" + textColor + RIGHT_J + ret;
                        data[7][i] = "" + textColor + RIGHT_J + amountWon;
                    }
                }
            }
            data[1][i] = "" + textColor + RIGHT_J + UtString.commaString(moneyHave);
        }
        int atX = posX + 2;
        posX += drawTable(playerHeaders, data, posX, posY, 8, colors[PAYOFF_TABLE_COLOR]) + TABLE_GAP_X;
        for (int i = 0; i < engine.countPlayers(); i++)
            if (engine.getPlayer(i).isComputerPlayer())
            {
                g2d.setColor(colors[HIGHLIGHT_TEXT_COLOR]);
                g2d.setFont(smallFont);
                g2d.drawString("C", atX, posY + (i + 2) * 17 - 11);
            }
        //
        g2d.dispose();
        g.drawImage(screenBuffer, 0, 0, null);
    }
}

package gr.tvarsa.hrace.model;

import gr.tvarsa.hrace.utility.UtMath;

import java.util.ArrayList;
import java.util.List;

public class Bet
{
    public static final int NO_BET = 0;
    public static final int BET_WIN = 1;
    public static final int BET_SECOND = 2;
    public static final int BET_THIRD = 3;
    public static final int BET_ONE_TWO = 4;
    public static final int BET_FIRST_TWO = 5;
    public static final int BET_ONE_TWO_THREE = 6;
    public static final int BET_FIRST_THREE = 7;
    public static final int BET_NEW_RECORD = 8;

    public static final String[] BET_TYPES = {"No bet", "Win", "Second", "Third", "1-2", "Top 2", "1-2-3", "Top 3", "Record"};
    public static final String[] BET_TYPES_SHORT = {"Non", "Win", "Plc", "Sho", "12", "T2", "123", "T3", "Rec"};
    public static final int[] HORSES_BET = {0, 1, 1, 1, 2, 2, 3, 3, 0};

    private int amountBet = 0;
    private List<Horse> horses = new ArrayList<>();
    private int betType = NO_BET;
    private double payoff = 0;
    private int won = 0;

    public int getAmountBet()
    {
        return amountBet;
    }

    public void setAmountBet(int amount)
    {
        this.amountBet = amount;
    }

    public int getWon()
    {
        return won;
    }

    public void setWon(int won)
    {
        this.won = won;
    }

    public int getBetType()
    {
        return betType;
    }

    public void setBetType(int betType)
    {
        this.betType = betType;
    }

    public List<Horse> getHorses()
    {
        return horses;
    }

    public int countHorses()
    {
        return horses.size();
    }

    public Horse getHorse(int i)
    {
        if (UtMath.inRange(i, 0, countHorses() - 1)) return horses.get(i);
        return null;
    }

    public void addHorse(Horse horse)
    {
        horses.add(horse);
    }

    public double getPayoff()
    {
        return payoff;
    }

    public void setPayoff(double payoff)
    {
        this.payoff = payoff;
    }

    public Bet clone()
    {
        Bet bet = new Bet();
        bet.amountBet = this.amountBet;
        bet.horses = new ArrayList<>();
        for (int i = 0; i < this.horses.size(); i++)
            bet.horses.add(this.horses.get(i));
        bet.betType = this.betType;
        bet.payoff = this.payoff;
        bet.won = this.won;
        return bet;
    }
}

package gr.tvarsa.hrace.dto;

import gr.tvarsa.hrace.model.Bet;
import gr.tvarsa.hrace.model.Horse;
import gr.tvarsa.hrace.utility.UtMath;

import java.util.ArrayList;
import java.util.List;

public class BetResult
{
    private class HorseInfo
    {
        Horse horse;
        int performanceOrder;
        double payoff;
    }

    private List<HorseInfo> horseInfos = new ArrayList<>();
    private List<Horse> horsesByOrder = new ArrayList<>();
    private List<Horse> horsesByPerformance = new ArrayList<>();
    private double bestTime;

    public List<Horse> getHorsesByOrder()
    {
        return horsesByOrder;
    }

    public List<Horse> getHorsesByPerformance()
    {
        return horsesByPerformance;
    }

    private void createHorseInfos(List<Horse> originalHorses)
    {
        horseInfos = new ArrayList<>();
        horsesByPerformance = new ArrayList<>();
        horsesByOrder = new ArrayList<>();
        if (originalHorses.size() < 2) return;
        List<Horse> tmp = new ArrayList<>();
        tmp.addAll(originalHorses);
        while (tmp.size() > 0)
        {
            double order = Integer.MAX_VALUE;
            int at = -1;
            for (int i = 0; i < tmp.size(); i++)
            {
                if (at < 0 || tmp.get(i).getFinishOrder() < order)
                {
                    order = tmp.get(i).getFinishOrder();
                    at = i;
                }
            }
            if (at < 0) break;
            horsesByOrder.add(tmp.get(at));
            tmp.remove(at);
        }
        tmp = new ArrayList<>();
        tmp.addAll(originalHorses);
        while (tmp.size() > 0)
        {
            double performance = -1;
            int at = -1;
            for (int i = 0; i < tmp.size(); i++)
            {
                if (at < 0 || tmp.get(i).getPerformance() > performance)
                {
                    performance = tmp.get(i).getPerformance();
                    at = i;
                }
            }
            if (at < 0) break;
            horsesByPerformance.add(tmp.get(at));
            tmp.remove(at);
        }
        for (int i = 0; i < horsesByPerformance.size(); i++)
        {
            Horse orderedHorse = horsesByPerformance.get(i);
            int order = -1;
            for (int j = 0; j < originalHorses.size(); j++)
            {
                Horse originalHorse = originalHorses.get(j);
                if (orderedHorse != originalHorse) continue;
                order = j + 1;
                break;
            }
            if (order == -1) continue;
            HorseInfo info = new HorseInfo();
            info.horse = orderedHorse;
            info.performanceOrder = order;
            double payoff;
            if (i == 0)
            {
                double gap = orderedHorse.getPerformance() - horsesByPerformance.get(1).getPerformance();
                payoff = Math.max(1.1, 2 - gap / 8.0);
            }
            else
            {
                double gap = horseInfos.get(0).horse.getPerformance() - orderedHorse.getPerformance();
                double extra = gap / 1.8; // affects payoffs close to base payoff
                payoff = horseInfos.get(0).payoff + extra * extra / 4.0; // affects payoffs much larger than base payoff
            }
            info.payoff = UtMath.round(payoff, 1);
            orderedHorse.setWinPayoff(info.payoff);
            horseInfos.add(info);
        }
    }

    public BetResult(List<Horse> horses)
    {
        setHorses(horses);
    }

    public BetResult()
    {
        setHorses(null);
    }

    public void setBestTime(int bestTimeSec)
    {
        this.bestTime = bestTimeSec / 1000.0;
    }

    public void setBestTime(double bestTimeMilli)
    {
        this.bestTime = bestTimeMilli;
    }

    public void setHorses(List<Horse> horses)
    {
        if (horses == null) horses = new ArrayList<>();
        createHorseInfos(horses);
    }

    public double getBetReturn(Bet bet)
    {
        ArrayList<Horse> payableHorses = keepPayable(horsesByOrder);
        if (bet == null || payableHorses.size() == 0) return 0.0;
        int typeOfBet = bet.getBetType();
        if (typeOfBet == Bet.NO_BET) return 0.0;
        Horse betHorse0 = null;
        Horse betHorse1 = null;
        Horse betHorse2 = null;
        Horse winHorse0 = null;
        Horse winHorse1 = null;
        Horse winHorse2 = null;
        if (typeOfBet != Bet.BET_NEW_RECORD)
        {
            if (bet.countHorses() == 0) return 0.0;
            if (bet.countHorses() > 0) betHorse0 = bet.getHorse(0);
            if (betHorse0 == null) return 0.0;
            if (bet.countHorses() > 1) betHorse1 = bet.getHorse(1);
            if (bet.countHorses() > 2) betHorse2 = bet.getHorse(2);
            if (payableHorses.size() > 0) winHorse0 = payableHorses.get(0);
            if (winHorse0 == null) return 0.0;
            if (payableHorses.size() > 1) winHorse1 = payableHorses.get(1);
            if (payableHorses.size() > 2) winHorse2 = payableHorses.get(2);
        }
        else if (payableHorses.size() > 0) winHorse0 = payableHorses.get(0);
        double result = 0.0;
        switch (typeOfBet)
        {
            case Bet.BET_NEW_RECORD:
                if (winHorse0 != null && winHorse0.getFinishTime() < bestTime) result = 1.0;
                break;
            case Bet.BET_WIN:
                if (betHorse0 == winHorse0)
                    result = 1.0;
                else if (betHorse0 == winHorse1) result = 0.1;
                break;
            case Bet.BET_SECOND:
                if (betHorse0 == winHorse1)
                    result = 1.0;
                else if (betHorse0 == winHorse0)
                    result = 0.1;
                else if (betHorse0 == winHorse2) result = 0.1;
                break;
            case Bet.BET_THIRD:
                if (betHorse0 == winHorse2)
                    result = 1.0;
                else if (betHorse0 == winHorse1) result = 0.1;
                break;
            case Bet.BET_ONE_TWO:
                if (betHorse1 == null)
                    result = 0.0;
                else if (winHorse0 == betHorse0 && winHorse1 == betHorse1)
                    result = 1.0;
                else if (winHorse0 == betHorse1 && winHorse1 == betHorse0)
                    result = 0.1;
                else
                {
                    if (winHorse0 == betHorse0) result = 0.1;
                    if (winHorse1 == betHorse1) result += 0.05;
                }
                break;
            case Bet.BET_FIRST_TWO:
                if (betHorse1 == null)
                    result = 0.0;
                else if (winHorse0 == betHorse0 && winHorse1 == betHorse1 || winHorse0 == betHorse1 && winHorse1 == betHorse0)
                    result = 1.0;
                else if (winHorse0 == betHorse0 || winHorse1 == betHorse1 || winHorse0 == betHorse1 || winHorse1 == betHorse0)
                    result = 0.1;
                break;
            case Bet.BET_ONE_TWO_THREE:
                if (betHorse1 == null || betHorse2 == null || winHorse1 == null || winHorse2 == null)
                    result = 0.0;
                else
                {
                    int matches = 0;
                    if (betHorse0 == winHorse0) matches++;
                    if (betHorse1 == winHorse1) matches++;
                    if (betHorse2 == winHorse2) matches++;
                    if (matches == 3)
                        result = 1.0;
                    else if (matches == 2)
                        result = 0.1;
                    else
                    {
                        matches = 0;
                        if (betHorse0 == winHorse1) matches++;
                        if (betHorse1 == winHorse2 || betHorse1 == winHorse0) matches++;
                        if (betHorse2 == winHorse1) matches++;
                        if (matches == 3) result = 0.15;
                        if (matches == 2) result = 0.05;
                    }
                }
                break;
            case Bet.BET_FIRST_THREE:
                if (betHorse1 == null || betHorse2 == null || winHorse1 == null || winHorse2 == null)
                    result = 0.0;
                else
                {
                    int matches = 0;
                    if (betHorse0 == winHorse0 || betHorse0 == winHorse1 || betHorse0 == winHorse2) matches++;
                    if (betHorse1 == winHorse0 || betHorse1 == winHorse1 || betHorse1 == winHorse2) matches++;
                    if (betHorse2 == winHorse0 || betHorse2 == winHorse1 || betHorse2 == winHorse2) matches++;
                    if (matches == 3)
                        result = 1.0;
                    else if (matches == 2) result = 0.1;
                }
                break;
        }
        return result;
    }

    public double getPayoff(Bet bet)
    {
        if (bet == null) return 0.0;
        if (bet.countHorses() == 0 && bet.getBetType() != Bet.BET_NEW_RECORD) return 0.0;
        Horse horse0 = null;
        Horse horse1 = null;
        Horse horse2 = null;
        if (bet.countHorses() > 0) horse0 = bet.getHorse(0);
        if (bet.countHorses() > 1) horse1 = bet.getHorse(1);
        if (bet.countHorses() > 2) horse2 = bet.getHorse(2);
        return getPayoff(bet.getBetType(), horse0, horse1, horse2);
    }

    public double getPayoff(int typeOfBet)
    {
        ArrayList<Horse> payableHorses = keepPayable(horsesByOrder);
        if (payableHorses.size() == 0) return 0.0;
        Horse horse0 = null;
        Horse horse1 = null;
        Horse horse2 = null;
        if (payableHorses.size() > 0) horse0 = payableHorses.get(0);
        if (payableHorses.size() > 1) horse1 = payableHorses.get(1);
        if (payableHorses.size() > 2) horse2 = payableHorses.get(2);
        return getPayoff(typeOfBet, horse0, horse1, horse2);
    }

    public double getPayoff(int typeOfBet, Horse horse0, Horse... horses)
    {
        ArrayList<Horse> payableHorses = keepPayable(horsesByOrder);
        if (payableHorses.size() == 0 || horse0 == null) return 0.0;
        double payoff0 = 1.0;
        double payoff1 = 1.0;
        double payoff2 = 1.0;
        if (typeOfBet != Bet.BET_NEW_RECORD)
        {
            if (payableHorses.size() == 1) return 1.0;
            payoff0 = payableHorses.get(0).getWinPayoff();
            payoff1 = payableHorses.get(1).getWinPayoff();
            payoff2 = 1.0;
            if (payableHorses.size() >= 3) payoff2 = payableHorses.get(2).getWinPayoff();
        }
        double result = 0.0;
        switch (typeOfBet)
        {
            case Bet.BET_NEW_RECORD:
                int maxLevel = -1;
                for (int i = 0; i < payableHorses.size(); i++)
                    if (payableHorses.get(i).getLevel() > maxLevel) maxLevel = payableHorses.get(i).getLevel();
                int inMaxLevel = 0;
                for (int i = 0; i < payableHorses.size(); i++)
                    if (payableHorses.get(i).getLevel() == maxLevel) inMaxLevel++;
                result = 2 + (payableHorses.size() + 1.0) / (inMaxLevel + 1.0);
                break;
            case Bet.BET_WIN:
                result = horse0.getWinPayoff();
                break;
            case Bet.BET_SECOND:
                result = horse0.getWinPayoff() * payoff1 / payoff0 * 1.25;
                break;
            case Bet.BET_THIRD:
                result = horse0.getWinPayoff() * payoff2 / payoff0 * 1.67;
                break;
            case Bet.BET_ONE_TWO:
                if (horses.length >= 1 && horses[0] != null) result = horse0.getWinPayoff() * horses[0].getWinPayoff() / 1.33;
                break;
            case Bet.BET_FIRST_TWO:
                if (horses.length >= 1 && horses[0] != null) result = horse0.getWinPayoff() * horses[0].getWinPayoff() / 2.0;
                break;
            case Bet.BET_ONE_TWO_THREE:
                if (horses.length >= 2 && horses[0] != null && horses[1] != null)
                    result = horse0.getWinPayoff() * horses[0].getWinPayoff() * horses[1].getWinPayoff() / 1.67;
                break;
            case Bet.BET_FIRST_THREE:
                if (horses.length >= 2 && horses[0] != null && horses[1] != null)
                    result = horse0.getWinPayoff() * horses[0].getWinPayoff() * horses[1].getWinPayoff() / (1.67 * 1.67);
                break;
        }
        if (result < 1.1 && result > 0) result = 1.1;
        return UtMath.round(result, 1);
    }

    public String getPayoffHorses(Bet bet)
    {
        if (bet == null) return "";
        Horse horse0 = null;
        Horse horse1 = null;
        Horse horse2 = null;
        if (bet.countHorses() > 0) horse0 = bet.getHorse(0);
        if (bet.countHorses() > 1) horse1 = bet.getHorse(1);
        if (bet.countHorses() > 2) horse2 = bet.getHorse(2);
        if (bet.getBetType() == Bet.BET_SECOND || bet.getBetType() == Bet.BET_THIRD)
        {
            horse1 = horse0;
            horse2 = horse0;
        }
        return getPayoffHorses(bet.getBetType(), horse0, horse1, horse2);
    }

    private ArrayList<Horse> keepPayable(List<Horse> horses)
    {
        if (horses == null) return new ArrayList<>();
        ArrayList<Horse> oayable = new ArrayList<>(horses);
        for (int i = oayable.size() - 1; i >= 0; i--)
            if (oayable.get(i) == null || !oayable.get(i).paysBet()) oayable.remove(i);
        return oayable;
    }

    public String getPayoffHorses(int typeOfBet)
    {
        ArrayList<Horse> payableHorses = keepPayable(horsesByOrder);
        Horse horse0 = null;
        Horse horse1 = null;
        Horse horse2 = null;
        if (payableHorses.size() > 0) horse0 = payableHorses.get(0);
        if (payableHorses.size() > 1) horse1 = payableHorses.get(1);
        if (payableHorses.size() > 2) horse2 = payableHorses.get(2);
        return getPayoffHorses(typeOfBet, horse0, horse1, horse2);
    }

    public String getPayoffHorses(int typeOfBet, Horse horse0, Horse... horses)
    {
        char horse0No = '?';
        if (horse0 != null) horse0No = (char)('0' + horse0.getNumber());
        char horse1No = '?';
        if (horses.length > 0 && horses[0] != null) horse1No = (char)('0' + horses[0].getNumber());
        char horse2No = '?';
        if (horses.length > 1 && horses[1] != null) horse2No = (char)('0' + horses[1].getNumber());
        String result = "?";
        switch (typeOfBet)
        {
            case Bet.BET_NEW_RECORD:
                result = "time";
                break;
            case Bet.BET_WIN:
                result = "" + horse0No;
                break;
            case Bet.BET_SECOND:
                result = "" + horse1No;
                break;
            case Bet.BET_THIRD:
                result = "" + horse2No;
                break;
            case Bet.BET_ONE_TWO:
                result = horse0No + "-" + horse1No;
                break;
            case Bet.BET_FIRST_TWO:
                if (horse0No < horse1No)
                    result = horse0No + "," + horse1No;
                else
                    result = horse1No + "," + horse0No;
                break;
            case Bet.BET_ONE_TWO_THREE:
                result = horse0No + "-" + horse1No + "-" + horse2No;
                break;
            case Bet.BET_FIRST_THREE:
                result = "";
                if (horse0No < horse1No && horse0No < horse2No)
                    result += horse0No;
                else if (horse1No < horse2No && horse1No < horse2No)
                    result += horse1No;
                else
                    result += horse2No;
                result += ",";
                if (horse0No > horse1No && horse0No < horse2No || horse0No < horse1No && horse0No > horse2No)
                    result += horse0No;
                else if (horse1No > horse0No && horse1No < horse2No || horse1No < horse0No && horse1No > horse2No)
                    result += horse1No;
                else
                    result += horse2No;
                result += ",";
                if (horse0No > horse1No && horse0No > horse2No)
                    result += horse0No;
                else if (horse1No > horse2No && horse1No > 1)
                    result += horse1No;
                else
                    result += horse2No;
                break;
        }
        return result;
    }
}
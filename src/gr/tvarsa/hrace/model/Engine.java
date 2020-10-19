package gr.tvarsa.hrace.model;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.dto.BetResult;
import gr.tvarsa.hrace.dto.HistoryStep;
import gr.tvarsa.hrace.gui.*;
import gr.tvarsa.hrace.utility.*;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Engine
{
    public static final String NEW_RACE_MARK = "================================================ Race no ";
    public static final String GAP = "       ";
    public static final String SPLIT = ">>";

    public static final int MAX_PLAYERS = 8;
    public static final int MIN_RACE_HORSES = 4;
    public static final int MAX_RACE_HORSES = 8;
    public static final int MIN_RACE_FURLONGS = 5;
    public static final int MAX_RACE_FURLONGS = 8;
    public static final int MIN_HORSE_LEVEL = 1;
    public static final int MAX_HORSE_LEVEL = 10;

    public static final int RUN_AGAIN = -1; // must be negative!
    public static final int QUIT = -2;

    private static final double MIN_RUNNING_SPEED = 10.0;
    private static final int STATIC_HISTORY_STEPS = 1;
    private static final String ZERO_SPEED = "      0.00";
    private static final boolean DEBUG_ON = true;

    public enum Stage
    {
        INIT_GRAPHICS, PREPARING, SHOWING_RACE_INFO, GETTING_BETS, RACING, RACING_ENDED, FINAL_RESULTS_AVAILABLE
    }

    private static final double MAX_HORSE_SPEED_PER_MS = 0.01666667; // 60 km/h

    private List<Horse> horses = new ArrayList<>();
    private int raceFurlongs;
    private int simulationTime;
    private double bestTimes[][] = new double[MAX_RACE_FURLONGS - MIN_RACE_FURLONGS + 1][MAX_HORSE_LEVEL - MIN_HORSE_LEVEL + 1];
    private double bestSpeeds[][] = new double[MAX_RACE_FURLONGS - MIN_RACE_FURLONGS + 1][MAX_HORSE_LEVEL - MIN_HORSE_LEVEL
            + 1];
    private String raceClassification = "";
    private int raceNo = 0;
    private String horseOrder = "";
    private String maxSpeedStr = ZERO_SPEED;
    private List<HistoryStep> historySteps = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private Stage stage = Stage.INIT_GRAPHICS;
    private int historyStep = -1;

    public Engine()
    {
        Horse.loadImages();
    }

    public static double furlongsToLength(double furlongs)
    {
        return furlongs * HorjeRace.raceOptions.getFurlongLengthValue();// 225.916;
    }

    public void writeln(String s)
    {
        write(s + "\n");
    }

    public void writeln()
    {
        write("\n");
    }

    private void write(String s)
    {
        if (HorjeRace.display != null) HorjeRace.display.write(s);
    }

    public int getRaceNo()
    {
        return raceNo;
    }

    public void setRaceNo(int raceNo)
    {
        this.raceNo = raceNo;
    }

    public Stage getStage()
    {
        return stage;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
        if (!stageForChanges())
        {
            HorjeRace.raceOptions.setVisible(false);
            HorjeRace.playerOptions.setVisible(false);
        }
    }

    public boolean stageForChanges()
    {
        return stage == Stage.INIT_GRAPHICS || stage == Stage.PREPARING || stage == Stage.SHOWING_RACE_INFO
                || stage == Stage.RACING_ENDED || stage == Stage.FINAL_RESULTS_AVAILABLE;
    }

    public double getBestTimes(int furlongs, int horseLevel)
    {
        if (!UtMath.inRange(furlongs, MIN_RACE_FURLONGS, MAX_RACE_FURLONGS)) return -1;
        if (!UtMath.inRange(horseLevel, MIN_HORSE_LEVEL, MAX_HORSE_LEVEL)) return -1;
        return bestTimes[furlongs - MIN_RACE_FURLONGS][horseLevel - MIN_HORSE_LEVEL];
    }

    public void setBestTimes(int raceLength, int horseLevel, double time)
    {
        if (!UtMath.inRange(raceLength, MIN_RACE_FURLONGS, MAX_RACE_FURLONGS)) return;
        if (!UtMath.inRange(horseLevel, MIN_HORSE_LEVEL, MAX_HORSE_LEVEL)) return;
        bestTimes[raceLength - MIN_RACE_FURLONGS][horseLevel - MIN_HORSE_LEVEL] = time;
    }

    private void setBestSpeeds(int raceLength, int horseLevel, double speed)
    {
        if (!UtMath.inRange(raceLength, MIN_RACE_FURLONGS, MAX_RACE_FURLONGS)) return;
        if (!UtMath.inRange(horseLevel, MIN_HORSE_LEVEL, MAX_HORSE_LEVEL)) return;
        bestSpeeds[raceLength - MIN_RACE_FURLONGS][horseLevel - MIN_HORSE_LEVEL] = speed;
    }

    private double getBestSpeeds(int furlongs, int horseLevel)
    {
        if (!UtMath.inRange(furlongs, MIN_RACE_FURLONGS, MAX_RACE_FURLONGS)) return -1;
        if (!UtMath.inRange(horseLevel, MIN_HORSE_LEVEL, MAX_HORSE_LEVEL)) return -1;
        double speed = bestSpeeds[furlongs - MIN_RACE_FURLONGS][horseLevel - MIN_HORSE_LEVEL];
        if (speed <= 0) return -1;
        return speed;
    }

    private Point.Double getBestSpeeds(int furlongs)
    {
        Point.Double best = new Point.Double(-1, -1);
        if (!UtMath.inRange(furlongs, MIN_RACE_FURLONGS, MAX_RACE_FURLONGS)) return best;
        for (int i = MIN_HORSE_LEVEL; i <= MAX_HORSE_LEVEL; i++)
        {
            double d = getBestSpeeds(furlongs, i);
            if (d > best.x)
            {
                best.x = d;
                best.y = i;
            }
        }
        return best;
    }

    public void setRandomBestTimes()
    {
        RaceOptions raceOptions = HorjeRace.raceOptions;
        RaceOptions options = new RaceOptions();
        options.setMinHorsesValue(1);
        options.setMaxHorsesValue(1);
        options.setTimeFactorValue(raceOptions.getTimeFactorValue());
        options.setLoopPauseValue(0);
        options.setInjuryRateValue(0);
        options.setDriftRateValue(0);
        options.setRandomnessValue(raceOptions.getRandomnessValue());
        options.setFurlongLengthValue(raceOptions.getFurlongLengthValue());
        for (int i = MIN_RACE_FURLONGS; i <= MAX_RACE_FURLONGS; i++)
        {
            options.setRaceDistanceRange(i, i);
            for (int j = MIN_HORSE_LEVEL; j <= MAX_HORSE_LEVEL; j++)
            {
                options.setHorseLevelRange(j, j);
                setBestTimes(i, j, runRace(options, false));
            }
        }
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public int countPlayers()
    {
        return players.size();
    }

    public int countBettingPlayers()
    {
        if (players == null) return 0;
        int count = 0;
        for (int i = 0; i < countPlayers(); i++)
            if (players.get(i).getMoney() > 0) count++;
        return count;
    }

    public Player getPlayer(int i)
    {
        if (UtMath.inRange(i, 0, countPlayers() - 1)) return players.get(i);
        return null;
    }

    public void addPlayer(Player player)
    {
        if (countPlayers() < MAX_PLAYERS) players.add(player);
    }

    public List<HistoryStep> getHistorySteps()
    {
        return historySteps;
    }

    public int countHistorySteps()
    {
        return historySteps.size();
    }

    public HistoryStep getHistoryStep(int i)
    {
        if (UtMath.inRange(i, 0, countHistorySteps() - 1)) return historySteps.get(i);
        return null;
    }

    public int getHistoryStep()
    {
        return historyStep;
    }

    private void recordHistoryStep()
    {
        HistoryStep step = new HistoryStep();
        step.setTime(getSimulationTime() / 1000.0);
        step.setOrder(horseOrder);
        step.setSpeed(UtString.justifyRight(getMaxSpeedStr(false), 8));
        for (int i = 0; i < countHorses(); i++)
        {
            Horse horse = horses.get(i);
            step.setHorseImage(i, horse.getCurrentImageIndex());
            step.setXPos(i, horse.getXPos());
            step.setYPosHeightDrift(i, horse.getYPosHeightDrift());
        }
        historySteps.add(step);
    }

    public String getRaceClassification()
    {
        return raceClassification;
    }

    public void setRaceClassification(int raceFurlongs, int maxHorseLevel)
    {
        this.raceClassification = "F" + raceFurlongs + "-L" + maxHorseLevel;
    }

    public int getRaceFurlongs()
    {
        return raceFurlongs;
    }

    public Horse getHorse(int index)
    {
        if (index < 0 || index >= horses.size()) return null;
        return horses.get(index);
    }

    public int countHorses()
    {
        return horses.size();
    }

    private void pauseAndDisplay(int pause)
    {
        if (HorjeRace.display == null) return;
        HorjeRace.display.repaint();
        UtCpu.pause(pause);
    }

    private void setupHistoryHorses()
    {
        HistoryStep step = historySteps.get(historyStep);
        for (int i = 0; i < countHorses(); i++)
        {
            Horse horse = getHorse(i);
            horse.setXPos(step.getXPos(i));
            horse.setYPosHeightDrift(step.getYPosHeightDrift(i));
            step.getHorseImage(horse, i);
        }
    }

    public void positionCameraX()
    {
        HorjeDisplay display = HorjeRace.display;
        double raceDistance = furlongsToLength(raceFurlongs);
        double cameraX = display.getCameraPanel().getCameraX();
        double halfScreen = CameraPanel.toMeters(display.getCameraPanel().getWidth() / 2);
        double maxHorseX = Double.NEGATIVE_INFINITY;
        double minHorseX = Double.MAX_VALUE;
        for (int i = 0; i < countHorses(); i++)
        {
            double at = horses.get(i).getFaceX();
            if (at > maxHorseX) maxHorseX = at;
            at = horses.get(i).getXPos();
            if (at < minHorseX) minHorseX = at;
        }
        double minCameraX = -3.46;
        double maxCameraX = raceDistance + 8 - 2 * halfScreen;
        double targetCameraX = maxHorseX - halfScreen - cameraX;
        double stepX = targetCameraX / 12.0;
        double localDistance = minCameraX + halfScreen;
        if (cameraX < localDistance && stepX < 0) stepX = stepX * (cameraX - minCameraX) / localDistance;
        localDistance = halfScreen;
        if (maxCameraX - cameraX < localDistance && stepX > 0) stepX = stepX * (maxCameraX - cameraX) / localDistance;
        // if (gr.tvarsa.hrace.gui.CameraPanel.toPixels(Math.abs(stepX)) < 1) stepX = 0;
        cameraX = cameraX + stepX;
        if (cameraX > maxCameraX) cameraX = maxCameraX;
        if (cameraX < minCameraX) cameraX = minCameraX;
        display.getCameraPanel().setCameraX(cameraX);
    }

    public void runReplay(String... statusAtExit)
    {
        ReplayControl control = HorjeRace.replayControl;
        HorjeDisplay display = HorjeRace.display;
        display.setStatus("Replay" + GAP + SPLIT + GAP + "Use replay control");
        control.setVisible(true);
        int x = display.getLocation().x + (display.getWidth() - control.getWidth()) / 2;
        int y = display.getLocation().y + display.getHeight();
        y = Math.min(y, UtGui.getScreenBounds().bottom - control.getHeight());
        control.setLocation(x, y);
        historyStep = historySteps.size() - 1;
        boolean updateAction = true;
        double updateTime = 0;
        control.setUserMoveAction(ReplayControl.ACTION_STOP, true);
        int flash = 0;
        display.getCameraPanel().setFlashReplay(true);
        while (true)
        {
            setupHistoryHorses();
            int keyCode = display.getKeyCode();
            if (keyCode == KeyEvent.VK_ESCAPE) control.setVisible(false);
            if (!control.isVisible()) break;
            updateTime += control.getSpeedSliderValue();
            updateAction = false;
            while (updateTime >= 1.0)
            {
                updateAction = true;
                updateTime -= 1.0;
            }
            if (control.isUserAction()) updateAction = true;
            if (updateAction)
            {
                int action = control.getMoveAction();
                if (action == ReplayControl.ACTION_MOVE_ONE)
                    historyStep += 1;
                else if (action == ReplayControl.ACTION_MOVE_ONE_BACK)
                    historyStep += -1;
                else if (action == ReplayControl.ACTION_MOVE_MANY)
                {
                    historyStep += 4;
                    updateTime = 1.0;
                }
                else if (action == ReplayControl.ACTION_MOVE_MANY_BACK)
                {
                    historyStep += -4;
                    updateTime = 1.0;
                }
                else if (action == ReplayControl.ACTION_GO_TO_START)
                    historyStep = STATIC_HISTORY_STEPS - 1;
                else if (action == ReplayControl.ACTION_GO_TO_END)
                    historyStep = historySteps.size() - STATIC_HISTORY_STEPS;
                else if (action == ReplayControl.ACTION_GO_TO_ABSOLUTE) historyStep = control.getTimeSliderStep();
                historyStep = UtMath.ranged(historyStep, 0, historySteps.size() - 1);
                if (action != ReplayControl.ACTION_GO_TO_ABSOLUTE) control.setTimeSliderStep(historyStep);
                if (historyStep == 0 || historyStep == historySteps.size() - 1)
                {
                    if (control.getLoopAtEndCheck().isSelected()
                            && (action == ReplayControl.ACTION_MOVE_ONE_BACK || action == ReplayControl.ACTION_MOVE_MANY_BACK
                                    || action == ReplayControl.ACTION_MOVE_ONE || action == ReplayControl.ACTION_MOVE_MANY))
                    {
                        setupHistoryHorses();
                        pauseAndDisplay(500);
                        if (historyStep == historySteps.size() - 1)
                            historyStep = 0;
                        else
                            historyStep = historySteps.size() - 1;
                    }
                    else
                    {
                        control.setUserMoveAction(ReplayControl.ACTION_STOP, true);
                        control.setToggled(null);
                    }
                }
            }
            positionCameraX();
            flash += HorjeRace.raceOptions.getLoopPauseValue();
            if (flash >= 800)
            {
                flash = 0;
                display.getCameraPanel().toggleFlashReplay();
            }
            pauseAndDisplay(HorjeRace.raceOptions.getLoopPauseValue());
        }
        historyStep = historySteps.size() - 1;
        setupHistoryHorses();
        historyStep = -1;
        display.getCameraPanel().setFlashReplay(false);
        double from = getLastHorse().getXPos();
        double to = getFirstHorse().getFaceX();
        double visible = CameraPanel.toMeters(display.getWidth());
        double cameraX = display.getCameraPanel().getCameraX();
        double fromVisible = cameraX;
        double toVisible = cameraX + visible;
        if (from < fromVisible || to > toVisible) cameraX = to - visible / 2.0;
        display.getCameraPanel().setCameraX(cameraX);
        display.repaint();
        if (statusAtExit != null && statusAtExit.length > 0) display.setStatus(statusAtExit[0]);
    }

    public int getSimulationTime()
    {
        return simulationTime;
    }

    public Horse getFirstHorse()
    {
        int noOfHorses = horses.size();
        if (horses.size() == 0) return null;
        Horse firstHorse = horses.get(0);
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = horses.get(i);
            if (horse.getFinishOrder() == 1) return horse;
            if (horse.getFaceX() > firstHorse.getFaceX()) firstHorse = horse;
        }
        return firstHorse;
    }

    public Horse getLastHorse()
    {
        int noOfHorses = horses.size();
        if (horses.size() == 0) return null;
        Horse lastHorse = horses.get(0);
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = horses.get(i);
            if (horse.getFinishOrder() == 1) return horse;
            if (horse.getFaceX() < lastHorse.getFaceX()) lastHorse = horse;
        }
        return lastHorse;
    }

    public Horse getHistoryFirstHorse()
    {
        int noOfHorses = horses.size();
        if (horses.size() == 0 || historyStep < 0) return null;
        HistoryStep step = historySteps.get(historyStep);
        int at = 0;
        double firstFaceX = step.getFaceX(0);
        for (int i = 0; i < noOfHorses; i++)
        {
            double faceX = step.getFaceX(i);
            if (faceX > firstFaceX)
            {
                firstFaceX = faceX;
                at = i;
            }
        }
        return horses.get(at);
    }

    public Horse getHistoryNextToFinishHorse()
    {
        int noOfHorses = horses.size();
        double raceDistance = furlongsToLength(raceFurlongs);
        if (horses.size() == 0 || historyStep < 0) return null;
        HistoryStep step = historySteps.get(historyStep);
        int at = -1;
        double firstFaceX = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < noOfHorses; i++)
        {
            double faceX = step.getFaceX(i);
            if (faceX < raceDistance && faceX > firstFaceX)
            {
                firstFaceX = faceX;
                at = i;
            }
        }
        if (at >= 0) return horses.get(at);
        return null;
    }

    public String getBestSpeedStr()
    {
        Point.Double speed = getBestSpeeds(raceFurlongs);
        String s;
        if (speed.y <= 0 || speed.x <= 0)
            s = ZERO_SPEED;
        else
        {
            int level = (int)speed.y;
            s = "L" + level;
            if (level < 10) s += " ";
            s += UtString.roundStr(speed.x, 4, 2);
        }
        return s;
    }

    public String getMaxSpeedStr(boolean updateNow)
    {
        if (!updateNow) return maxSpeedStr;
        Horse horse = getFirstHorse();
        if (horse == null) return ZERO_SPEED;
        maxSpeedStr = ZERO_SPEED;
        if (getSimulationTime() > 0)
        {
            double speed;
            if (horse.getFinishOrder() > 0)
                speed = furlongsToLength(raceFurlongs) * 3.6 / horse.getFinishTime();
            else
                speed = horse.getFaceX() * 3600.0 / getSimulationTime();
            int level = horse.getLevel();
            maxSpeedStr = "L" + level;
            if (level < 10) maxSpeedStr += " ";
            maxSpeedStr += UtString.roundStr(speed, 4, 2);
        }
        return maxSpeedStr;
    }

    public List<Horse> getOrderedHorses()
    {
        int noOfHorses = horses.size();
        List<Horse> v = new ArrayList<>();
        double pos[] = new double[noOfHorses];
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = horses.get(i);
            if (horse.getFinishOrder() == 0 || horse.isDNF() || horse.isDSQ())
                pos[i] = horse.getFaceX();
            else
                pos[i] = 10000000 - horse.getFinishOrder();
        }
        for (int i = 0; i < noOfHorses; i++)
        {
            int k = 0;
            double m = pos[0];
            for (int j = 0; j < noOfHorses; j++)
                if (pos[j] > m)
                {
                    k = j;
                    m = pos[j];
                }
            v.add(horses.get(k));
            pos[k] = -1;
        }
        return v;
    }

    public String getHorseOrder(boolean updateNow)
    {
        if (!updateNow) return horseOrder;
        int noOfHorses = horses.size();
        double pos[] = new double[noOfHorses];
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = horses.get(i);
            if (horse.getFinishOrder() == 0 || horse.isDNF() || horse.isDSQ())
                pos[i] = horse.getFaceX();
            else
                pos[i] = 10000000 - horse.getFinishOrder();
        }
        horseOrder = "";
        for (int i = 0; i < noOfHorses; i++)
        {
            int k = 0;
            double m = pos[0];
            for (int j = 0; j < noOfHorses; j++)
                if (pos[j] > m)
                {
                    k = j;
                    m = pos[j];
                }
            horseOrder += horses.get(k).getNumber() + " ";
            pos[k] = -1;
        }
        return horseOrder;
    }

    public void reorderPlayers(boolean updateDisplay)
    {
        List<Player> v = new ArrayList<>();
        while (true)
        {
            int money = -1;
            int at = -1;
            for (int i = 0; i < players.size(); i++)
            {
                if (players.get(i).getMoney() > money)
                {
                    at = i;
                    money = players.get(i).getMoney();
                }
            }
            if (at >= 0)
            {
                v.add(players.get(at));
                players.remove(at);
            }
            if (players.size() == 0)
            {
                players.addAll(v);
                break;
            }
        }
        if (updateDisplay && HorjeRace.display != null) HorjeRace.display.repaint();
    }

    public void reorderHorses(boolean orderByPerformance, boolean updateDisplay)
    {
        List<Horse> v = new ArrayList<>();
        if (orderByPerformance)
            while (true)
            {
                double performance = -1.0;
                int at = -1;
                for (int i = 0; i < horses.size(); i++)
                {
                    if (horses.get(i).getPerformance() > performance)
                    {
                        at = i;
                        performance = horses.get(i).getPerformance();
                    }
                }
                if (at >= 0)
                {
                    v.add(horses.get(at));
                    horses.remove(at);
                }
                if (horses.size() == 0)
                {
                    horses.addAll(v);
                    break;
                }
            }
        else
            while (true)
            {
                int number = Integer.MAX_VALUE;
                int at = -1;
                for (int i = 0; i < horses.size(); i++)
                {
                    if (horses.get(i).getNumber() < number)
                    {
                        at = i;
                        number = horses.get(i).getNumber();
                    }
                }
                if (at >= 0)
                {
                    v.add(horses.get(at));
                    horses.remove(at);
                }
                if (horses.size() == 0)
                {
                    horses.addAll(v);
                    break;
                }
            }
        if (updateDisplay && HorjeRace.display != null) HorjeRace.display.repaint();
    }

    public double runRace(RaceOptions options, boolean showRace)
    {
        setStage(Stage.INIT_GRAPHICS);
        HorjeDisplay display = HorjeRace.display;
        BetResult betResult = HorjeRace.betResult;
        if (display == null || options == null) return RUN_AGAIN;
        if (showRace)
        {
            historySteps.clear();
            historyStep = -1;
            if (UtMath.isTrue(DEBUG_ON))
            {
                display.clipDebugOutput();
                writeln("\n" + NEW_RACE_MARK + raceNo + " ================================================\n");
                writeln("Seed = " + UtMath.getRandomSeed() + "L");
                writeln("Race init random = " + UtMath.rnd());
            }
            raceNo++;
        }
        for (int i = 0; i < countPlayers(); i++)
            players.get(i).getBets().clear();
        horses = new ArrayList<>();
        int noOfHorses = UtMath.rnd(options.getMinHorsesValue(), options.getMaxHorsesValue());
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = new Horse();
            horses.add(horse);
            horse.setNumber(i + 1);
            if (showRace) horse.createImages();
        }
        for (int i = 0; i < noOfHorses; i++)
            horses.get(i).getStoppedImage();
        setStage(Stage.PREPARING);
        simulationTime = 0;
        horseOrder = "";
        maxSpeedStr = ZERO_SPEED;
        getHorseOrder(true);
        int lastOrderUpdate = 0;
        double simulationTimeStep = 20.0;
        int pauseTime = options.getLoopPauseValue();
        boolean furlongs[] = options.getRaceDistanceChecks();
        do
            raceFurlongs = UtMath.rnd(MIN_RACE_FURLONGS, MAX_RACE_FURLONGS);
        while (furlongs[raceFurlongs - MIN_RACE_FURLONGS] == false);
        double raceDistance = furlongsToLength(raceFurlongs);
        double injuryRate = options.getInjuryRateValue();// 0 - 100
        injuryRate = 1 - injuryRate / 100.0 * injuryRate / 100.0
                / (raceDistance * 1.25 / (MAX_HORSE_SPEED_PER_MS * simulationTimeStep));
        double gameLuck = options.getRandomnessValue(); // 0-fastest one wins 100-totally unpredictable
        gameLuck = Math.pow(gameLuck / 100.0, 0.2) * 100;
        if (showRace && UtMath.isTrue(DEBUG_ON))
        {
            writeln("Game luck = " + gameLuck);
            // horse times table
            String s = "\nTIMES    ";
            for (int i = MIN_HORSE_LEVEL; i <= MAX_HORSE_LEVEL; i++)
                s += "lv=" + i + "    ";
            s += "\n------  ";
            for (int i = MIN_HORSE_LEVEL; i <= MAX_HORSE_LEVEL; i++)
                s += "------  ";
            s += "\n";
            for (int i = Engine.MIN_RACE_FURLONGS; i <= Engine.MAX_RACE_FURLONGS; i++)
            {
                s += UtString.justifyLeft("fur=" + i, 8);
                for (int j = MIN_HORSE_LEVEL; j <= MAX_HORSE_LEVEL; j++)
                    s += UtString.roundStr(getBestTimes(i, j), 2, 3) + "  ";
                s += "\n";
            }
            // horse speeds table
            s += "\nSPEEDS  ";
            for (int i = MIN_HORSE_LEVEL; i <= MAX_HORSE_LEVEL; i++)
                s += "lv=" + i + "   ";
            s += "\n------  ";
            for (int i = MIN_HORSE_LEVEL; i <= MAX_HORSE_LEVEL; i++)
                s += "-----  ";
            s += "\n";
            for (int i = Engine.MIN_RACE_FURLONGS; i <= Engine.MAX_RACE_FURLONGS; i++)
            {
                s += UtString.justifyLeft("fur=" + i, 8);
                for (int j = MIN_HORSE_LEVEL; j <= MAX_HORSE_LEVEL; j++)
                {
                    if (getBestSpeeds(i, j) > 0)
                        s += UtString.roundStr(getBestSpeeds(i, j), 2, 2) + "  ";
                    else
                        s += "       ";
                }
                s += "\n";
            }
            write(s);
            //
            int money = 0;
            int withMoney = 0;
            for (int i = 0; i < players.size(); i++)
                if (getPlayer(i).getMoney() > 0)
                {
                    money += getPlayer(i).getMoney();
                    withMoney++;
                }
            s = "";
            if (players.size() > 0)
            {
                s = "\nTotal money = " + UtString.commaString(money) + "    Players with money = " + withMoney + "/"
                        + players.size();
                if (withMoney > 0) s += "    Average = " + UtString.commaString(money / withMoney);
            }
            else
                s = "\nNo players";
            writeln(s);
        }
        int moving = noOfHorses;
        int horseLevel = 0;
        boolean levels[] = options.getHorseLevelChecks();
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = horses.get(i);
            horse.prepareForRace();
            int level;
            do
            {
                horse.setRandomPhysique();
                level = horse.getLevel();
            }
            while (levels[level - MIN_HORSE_LEVEL] == false);
            if (horse.getLevel() > horseLevel) horseLevel = horse.getLevel();
            if (!showRace)
                horse.setStartDelay(0);
            else
            {
                boolean nonLinear = !options.getLinearCheck().isSelected();
                double factor = 1.0;
                if (nonLinear)
                {
                    factor = UtMath.rnd(0.0, 1.0);
                    factor = factor * factor;
                }
                horse.setStartDelay(factor * UtMath.rnd(0, options.getMaxDelayValue() - options.getMinDelayValue())
                        + options.getMinDelayValue());
            }
            horse.setMinCurrSpeed(MIN_RUNNING_SPEED);
            horse.setMaxCurrSpeed(horse.getMaxRunSpeed());
            horse.setSpeed(0.0);
        }
        setRaceClassification(raceFurlongs, horseLevel);
        reorderHorses(display.getOrderHorsesCheck().isSelected(), showRace);
        betResult.setHorses(horses);
        betResult.setBestTime(getBestTimes(raceFurlongs, horseLevel));
        if (showRace)
        {
            if (UtMath.isTrue(DEBUG_ON))
            {
                String s = "\nRace furlongs = " + raceFurlongs + " (" + UtString.roundStr(furlongsToLength(raceFurlongs), 1)
                        + "m)      gr.tvarsa.hrace.model.Horse levels = ";
                for (int i = 0; i < betResult.getHorsesByPerformance().size(); i++)
                {
                    if (i > 0) s += "   ";
                    s += betResult.getHorsesByPerformance().get(i).getLevel();
                }
                writeln(s);
                writeln("\nH   MaxSp  MinSp  AvgSp  MaxSt  MinSt  AvgSt  Stat  Lv  WPay\n"
                        + "--  -----  -----  -----  -----  -----  -----  ----  --  ----");
                for (int i = 0; i < noOfHorses; i++)
                {
                    Horse horse = horses.get(i);
                    double avgSpeed = (horse.getMaxRunSpeed() + horse.getMinRunSpeed()) / 2;
                    double avgStamina = (horse.getMaxStamina() + horse.getMinStamina()) / 2;
                    writeln(UtString.justifyRight("" + horse.getNumber(), 2) + "  "
                            + UtString.roundStr(horse.getMaxRunSpeed(), 2) + "  " + UtString.roundStr(horse.getMinRunSpeed(), 2)
                            + "  " + UtString.roundStr(avgSpeed, 2) + "  " + UtString.roundStr(horse.getMaxStamina(), 2) + "  "
                            + UtString.roundStr(horse.getMinStamina(), 2) + "  " + UtString.roundStr(avgStamina, 2) + "  "
                            + UtString.roundStr(horse.getPerformance(), 1) + "  "
                            + UtString.justifyRight("" + horse.getLevel(), 2) + "  "
                            + UtString.justifyRight(UtString.roundStr(horse.getWinPayoff(), 1), 4));
                }
                writeln();
            }
            display.getInfoPanel().setHighlightedPlayer(null);
            display.getCameraPanel().setCameraX(horses.get(0).getXPos() - 1);
            display.getInfoPanel().setBestTime(getBestTimes(raceFurlongs, horseLevel));
            display.getInfoPanel().setRefreshMode(InfoPanel.FULL_REFRESH);
            display.repaint();
            int key = KeyEvent.VK_SPACE;
            String s = "SPACE - Start race" + GAP + "BACKSPACE - New race" + GAP + "Q - Quit";
            if (HorjeRace.raceOptions.countBetTypeChecks() > 0 && countBettingPlayers() > 0 && countHorses() >= 4)
            {
                s = "RETURN - Enter bets" + GAP + s;
                key = KeyEvent.VK_ENTER;
            }
            display.setStatus(s);
            setStage(Stage.SHOWING_RACE_INFO);
            display.repaint();
            key = UtGui.waitForKeys(display, KeyEvent.VK_Q, KeyEvent.VK_SPACE, KeyEvent.VK_BACK_SPACE, key);
            if (key == KeyEvent.VK_Q) return QUIT;
            if (key == KeyEvent.VK_BACK_SPACE)
            {
                stage = Stage.INIT_GRAPHICS;
                display.repaint();
                return RUN_AGAIN;
            }
            display.repaint();
            if (key == KeyEvent.VK_ENTER)
            {
                setStage(Stage.GETTING_BETS);
                getBets(true);
                display.getInfoPanel().setHighlightedPlayer(null);
                setStage(Stage.SHOWING_RACE_INFO);
                display.setStatus("SPACE - Start race");
                UtGui.waitForKeys(display, KeyEvent.VK_SPACE);
            }
            display.setStatus("Race on" + GAP + SPLIT + GAP + "R - Replay");
            for (int i = 0; i < STATIC_HISTORY_STEPS; i++)
                recordHistoryStep();
            for (int i = 0; i < noOfHorses; i++)
                horses.get(i).getRandomImage();
            display.getInfoPanel().setRefreshMode(InfoPanel.FULL_REFRESH);
        }
        else
        {
            setStage(Stage.GETTING_BETS);
            getBets(false);
            setStage(Stage.SHOWING_RACE_INFO);
        }
        if (showRace && UtMath.isTrue(DEBUG_ON)) writeln("Race start random = " + UtMath.rnd());

        UtChrono stopWatch = new UtChrono(false);
        setStage(Stage.RACING);
        boolean shownHeader = false;
        double winningTime = 10000.0;
        double framesTime = 0;
        int frames = 0;
        while (moving > 0)
        {
            stopWatch.restart();
            moving = 0;
            simulationTime += simulationTimeStep;
            for (int i = 0; i < noOfHorses; i++)
                horses.get(i).setMovedThisRound(false);
            for (int h = 0; h < noOfHorses; h++)
            {
                int tmp;
                do
                {
                    tmp = UtMath.rnd(0, noOfHorses - 1);
                }
                while (horses.get(tmp).isMovedThisRound());
                Horse horse = horses.get(tmp);
                horse.setStamina(horse.getStamina() - UtMath.rnd(0.0, 10.0) * simulationTimeStep / 1000.0);
                double stepSize = 0;
                if (horse.getFaceX() < raceDistance)
                {
                    if (simulationTime < horse.getStartDelay())
                        stepSize = 0;
                    else if (horse.isInjured())
                    {
                        horse.setMinCurrSpeed(0.0);
                        double maxSpeed = Math.max(0, horse.getSpeed() * (0.99 - UtMath.rnd(0.0, 0.005)));
                        horse.setMaxCurrSpeed(maxSpeed);
                        stepSize = horse.getSpeed();
                    }
                    else
                    {
                        if (horse.getSpeed() < horse.getMinRunSpeed())
                        {
                            horse.setMinCurrSpeed(
                                    Math.min(horse.getMinRunSpeed(), (horse.getSpeed() + 1) * UtMath.rnd(1.05, 1.30)));
                            stepSize = horse.getSpeed();
                        }
                        else
                        {
                            horse.setSpeed(horse.getSpeed() + UtMath.rnd(0.0, 25.0) - 12.5);
                            stepSize = (2 * horse.getSpeed() + horse.getStamina()) / 3.0;
                            if (UtMath.rnd(0.0, 100.0) < gameLuck) stepSize = UtMath.rnd(60.0, 100.0);
                        }
                    }
                }
                else
                {
                    horse.setMinCurrSpeed(0.0);
                    horse.setMaxCurrSpeed(Math.max(0, horse.getSpeed() * UtMath.rnd(0.94, 0.99) - 0.2));
                    stepSize = horse.getSpeed();
                }
                if (horse.getMaxCurrSpeed() < MIN_RUNNING_SPEED)
                {
                    horse.setCurrentImageIndex(0);
                }
                else
                {
                    moving++;
                    // max stepSize = 100
                    stepSize = MAX_HORSE_SPEED_PER_MS * simulationTimeStep * stepSize / 100;
                    horse.addXPos(stepSize);
                    if (stepSize == 0) horse.setCurrentImageIndex(0);
                    if (showRace)
                    {
                        if (horse.getFaceX() < raceDistance && UtMath.rnd() > injuryRate) //
                            // && gr.tvarsa.hrace.utility.UtGui.rnd(0, 100) * 2.6 < horse.getStamina() - 30
                            horse.setInjured(true);
                        double drift = options.getDriftRateValue() * 0.00032;
                        drift = horse.getYPosHeightDrift() + UtMath.rnd(-drift, drift);
                        if (!UtMath.inRange(drift, -0.5, 0.5)) horse.setDSQ(true);
                        drift = UtMath.ranged(drift, -0.5, 0.5);
                        horse.setYPosHeightDrift(drift);
                    }
                }
                if ((horse.getFaceX() >= raceDistance || horse.getMaxCurrSpeed() < MIN_RUNNING_SPEED)
                        && horse.getFinishOrder() == 0)
                {
                    int position;
                    double finishTime;
                    if (horse.getFaceX() >= raceDistance)
                    {
                        double y = horse.getFaceX() - raceDistance;
                        finishTime = (simulationTime - y / MAX_HORSE_SPEED_PER_MS) / 1000.0;
                        finishTime = UtMath.round(finishTime, 3);
                        position = 1;
                        //
                        // If the following commented lines are enabled, disqualified horses will be ordered last before DNF.
                        // If the are disabled, disqualified horses are ordered as normal ones, but are not bet payable.
                        //
                        // if (horse.isDSQ())
                        // {
                        // position = 8;
                        // for (int i = 0; i < noOfHorses; i++)
                        // {
                        // gr.tvarsa.hrace.model.Horse horse2 = horses.get(i);
                        // if ((horse2.isDNF() || horse2.isDSQ()) && horse2.getFinishOrder() > 0
                        // && horse2.getFinishOrder() <= position)
                        // position = horse2.getFinishOrder() - 1;
                        // }
                        // }
                        // else
                        for (int i = 0; i < noOfHorses; i++)
                        {
                            Horse horse2 = horses.get(i);
                            if (!horse2.isDNF() /* && !horse2.isDSQ() */ && horse2.getFinishOrder() > 0)
                            {
                                int finishOrder2 = horse2.getFinishOrder();
                                if (horse2.getFinishTime() > finishTime)
                                {
                                    if (finishOrder2 < position) position = finishOrder2;
                                    horse2.setFinishOrder(finishOrder2 + 1);
                                }
                                else if (finishOrder2 >= position) position = finishOrder2 + 1;
                            }
                        }
                    }
                    else
                    {
                        position = 8;
                        for (int i = 0; i < noOfHorses; i++)
                            if (horses.get(i).isDNF() && horses.get(i).getFinishOrder() <= position)
                                position = horses.get(i).getFinishOrder() - 1;
                        finishTime = UtMath.round((simulationTime + UtMath.rnd(0, 9)) / 1000.0, 3);
                        horse.setDNF(true);
                    }
                    horse.setFinished(true);
                    horse.setFinishTime(finishTime);
                    horse.setFinishOrder(position);
                    if (position == 1) winningTime = finishTime;
                    if (showRace && UtMath.isTrue(DEBUG_ON))
                    {
                        if (!shownHeader) writeln("\nP   H   Time     Cross  FinTim   MaxSp  MinSp  AvgSp  MaxSt  MinSt  Stam "
                                + "  AvgSt  Stat  Lv  Final  Comm\n--  --  -------  -----  -------  -----  -----  "
                                + "-----  -----  -----  -----  -----  ----  --  -----  ----");
                        shownHeader = true;
                        String s = "";
                        String cross = UtString.roundStr(horse.getFaceX() - raceDistance, 3);
                        if (horse.isInjured()) s = "inj";
                        if (horse.isDNF())
                        {
                            s = "DNF";
                            cross = "    -";
                        }
                        else if (horse.isDSQ())
                        {
                            s = "DSQ";
                            cross = "    -";
                        }
                        double avgSpeed = (horse.getMaxRunSpeed() + horse.getMinRunSpeed()) / 2;
                        double avgStamina = (horse.getMaxStamina() + horse.getMinStamina()) / 2;
                        writeln(UtString.padInt(position, 2, ' ') + "  " + UtString.padInt(horse.getNumber(), 2, ' ') + "  "
                                + UtString.roundStr(simulationTime / 1000.0, 3, 3) + "  " + cross + "  "
                                + UtString.roundStr(horse.getFinishTime(), 3, 3) + "  "
                                + UtString.roundStr(horse.getMaxRunSpeed(), 2) + "  "
                                + UtString.roundStr(horse.getMinRunSpeed(), 2) + "  " + UtString.roundStr(avgSpeed, 2) + "  "
                                + UtString.roundStr(horse.getMaxStamina(), 2) + "  "
                                + UtString.roundStr(horse.getMinStamina(), 2) + "  " + UtString.roundStr(horse.getStamina(), 2)
                                + "  " + UtString.roundStr(avgStamina, 2) + "  " + UtString.roundStr(horse.getPerformance(), 1)
                                + "  " + UtString.justifyRight("" + horse.getLevel(), 2) + "  "
                                + UtString.roundStr((2 * avgSpeed + horse.getStamina()) / 3, 2) + "  " + s);
                    }
                }
                horse.setMovedThisRound(true);
            }
            if (showRace)
            {
                if (simulationTime > lastOrderUpdate + 300)
                {
                    lastOrderUpdate += 300;
                    getHorseOrder(true);
                    if (lastOrderUpdate % 900 == 0) getMaxSpeedStr(true);
                }
                positionCameraX();
                recordHistoryStep();

                boolean measureFPS = true;
                int keyCode = display.getKeyCode();
                if (keyCode == KeyEvent.VK_R)
                {
                    measureFPS = false;
                    String s = display.getStatus();
                    runReplay();
                    display.setStatus("Replay ended" + GAP + SPLIT + GAP + "C - Continue race");
                    UtGui.waitForKeys(display, KeyEvent.VK_C);
                    display.setStatus(s);
                }

                display.repaint();
                double elapsedTime = stopWatch.getMilliseconds();
                if (measureFPS)
                {
                    framesTime += elapsedTime;
                    frames++;
                }
                int waitTime = Math.max(0, pauseTime - (int)elapsedTime);
                UtCpu.pause(waitTime);
            }
        }
        if (showRace) for (int i = 0; i < STATIC_HISTORY_STEPS; i++)
            recordHistoryStep();
        setStage(Stage.RACING_ENDED);
        simulationTime = (int)(winningTime * 1000);
        betResult.setHorses(horses);
        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            for (int j = 0; j < player.countBets(); j++)
            {
                Bet bet = player.getBet(j);
                double amount = 0;
                if (bet.getAmountBet() > 0)
                    amount = betResult.getPayoff(bet.getBetType()) * betResult.getBetReturn(bet) * bet.getAmountBet();
                bet.setWon((int)amount);
            }
        }
        if (showRace)
        {
            display.setStatus("End of race - awaiting results...");
            if (UtMath.isTrue(DEBUG_ON))
            {
                // horse time diferrences
                String s = "\nH   Perf  Lv  Time   +Prev   +First  Speed : Best   \n--  ----  -- ------- ------  ------  ----- - -----";
                for (int i = 0; i < betResult.getHorsesByOrder().size(); i++)
                {
                    s += "\n";
                    Horse horse = betResult.getHorsesByOrder().get(i);
                    s += UtString.justifyRight("" + horse.getNumber(), 2) + "  ";
                    s += UtString.roundStr(horse.getPerformance(), 2, 1) + "  ";
                    s += UtString.justifyRight("" + horse.getLevel(), 2) + "  ";
                    s += UtString.roundStr(horse.getFinishTime(), 2, 2) + "  ";
                    if (i > 0)
                    {
                        double timeDiff = horse.getFinishTime() - betResult.getHorsesByOrder().get(i - 1).getFinishTime();
                        s += UtString.justifyRight((timeDiff > 0 ? "+" : "") + UtString.roundStr(timeDiff, 2), 6) + "  ";
                    }
                    else
                        s += "     -  ";
                    if (i > 1)
                    {
                        double timeDiff = horse.getFinishTime() - betResult.getHorsesByOrder().get(0).getFinishTime();
                        s += UtString.justifyRight((timeDiff > 0 ? "+" : "") + UtString.roundStr(timeDiff, 2), 6) + "  ";
                    }
                    else
                        s += "     -  ";
                    if (horse.getFaceX() < raceDistance)
                        s += "    x       x";
                    else
                    {
                        double speed1 = raceDistance * 3.6 / horse.getFinishTime();
                        double speed2 = getBestSpeeds(raceFurlongs, horse.getLevel());
                        String tmp = "<";
                        if (speed1 == speed2)
                            tmp = "=";
                        else if (speed1 > speed2) tmp = ">";
                        s += UtString.roundStr(speed1, 2, 2) + " " + tmp + " " + UtString.roundStr(speed2, 2, 2);
                    }
                }
                writeln(s);
                writeln("\n" + frames + " main loop frames at " + UtString.roundStr(framesTime / frames, 3) + " ms/frame");
            }
            String s = "";
            if (countPlayers() > 0) s = "\nBET for   Money    Type    gr.tvarsa.hrace.model.Horse  gr.tvarsa.hrace.model.Bet      Ret   Pay   Won     \n"
                    + "--------  -------  ------  -----  -------  ----  ----  --------\n";
            for (int i = 0; i < countPlayers(); i++)
            {
                Player player = getPlayer(i);
                s += UtString.justifyLeft(player.getName(), 8) + "  ";
                if (player.countBets() == 0)
                    s += UtString.justifyRight(UtString.commaString(player.getMoney()), 7);
                else
                {
                    Bet bet = player.getBet(0);
                    s += UtString.justifyRight(UtString.commaString(player.getMoney() + bet.getAmountBet()), 7) + "  ";
                    if (bet.getBetType() != Bet.NO_BET)
                    {
                        s += UtString.justifyLeft(Bet.BET_TYPES[bet.getBetType()], 6) + "  ";
                        s += UtString.justifyLeft(betResult.getPayoffHorses(bet), 5) + "  ";
                        s += UtString.justifyRight(UtString.commaString(bet.getAmountBet()), 7) + "  ";
                        s += UtString.roundStr(betResult.getPayoff(bet.getBetType()), 2, 1) + "  ";
                        if (betResult.getBetReturn(bet) > 0)
                            s += UtString.roundStr(betResult.getBetReturn(bet), 1, 2) + "  ";
                        else
                            s += "   -  ";
                        int diff = bet.getWon() - bet.getAmountBet();
                        s += UtString.justifyRight((diff > 0 ? "+" : "") + UtString.commaString(diff), 8);
                    }
                }
                s += "\n";
            }
            write(s);
            UtCpu.pause(1.0);
        }
        setStage(Stage.FINAL_RESULTS_AVAILABLE);
        int keyCode = -1;
        if (showRace)
        {
            display.getInfoPanel().setRefreshMode(InfoPanel.FULL_REFRESH);
            display.repaint();
            while (true)
            {
                display.setStatus(
                        "End of race" + GAP + SPLIT + GAP + "RETURN - Restart" + GAP + "R - Replay" + GAP + "Q - Quit");
                keyCode = UtGui.waitForKeys(display, KeyEvent.VK_R, KeyEvent.VK_Q, KeyEvent.VK_ENTER);
                if (keyCode == KeyEvent.VK_R)
                    runReplay();
                else
                    break;
            }
        }
        for (int i = 0; i < getPlayers().size(); i++)
        {
            Player player = getPlayer(i);
            for (int j = player.countBets() - 1; j >= 0; j--)
            {
                Bet bet = player.getBet(j);
                player.setMoney(player.getMoney() + bet.getWon());
                player.getBets().remove(j);
            }
        }
        for (int i = 0; i < betResult.getHorsesByOrder().size(); i++)
        {
            if (i > 0) continue;// do it for first place only
            Horse horse = betResult.getHorsesByOrder().get(i);
            double speed = raceDistance * 3.6 / horse.getFinishTime();
            double previousBest = getBestSpeeds(raceFurlongs, horse.getLevel());
            if (previousBest <= 0 || speed > previousBest) setBestSpeeds(raceFurlongs, horse.getLevel(), speed);
        }
        if (winningTime < getBestTimes(raceFurlongs, horseLevel)) setBestTimes(raceFurlongs, horseLevel, winningTime);
        if (showRace)
        {
            if (display.getOrderPlayersCheck().isSelected()) reorderPlayers(true);
            if (keyCode == KeyEvent.VK_Q) return QUIT;
        }
        stage = Stage.INIT_GRAPHICS;
        return winningTime;
    }

    private int roundAmount(int amount)
    {
        if (amount == 0) return 0;
        int at = 10000000;
        if (amount >= at) return amount / (at / 10) * at / 10;
        while (true)
        {
            if (amount < at)
            {
                at /= 10;
                continue;
            }
            if (amount >= at * 5)
                return amount / (at * 5 / 10) * at * 5 / 10;
            else if (amount >= at * 2)
                return amount / (at * 2 / 10) * at * 2 / 10;
            else
                return amount / (at / 10) * at / 10;
        }
    }

    private int getOrderedHorsePos(List<Horse> orderedHorses, Horse horse)
    {
        for (int i = 0; i < orderedHorses.size(); i++)
            if (horse == orderedHorses.get(i)) return i;
        return -1;
    }

    private void setComputerBet(Player player, boolean showBets)
    {
        if (countBettingPlayers() < 1 || HorjeRace.raceOptions.countBetTypeChecks() == 0 || countHorses() < 4) return;
        List<Horse> orderedHorses = getOrderedHorses();
        if (orderedHorses.size() != horses.size()) return;
        double performanceOfOrderedHorse[] = new double[countHorses()];
        double differencesOfOrderedHorse[][] = new double[performanceOfOrderedHorse.length][performanceOfOrderedHorse.length];
        for (int i = 0; i < countHorses(); i++)
            performanceOfOrderedHorse[i] = orderedHorses.get(i).getPerformance();
        for (int i = 0; i < performanceOfOrderedHorse.length; i++)
            for (int j = 0; j < performanceOfOrderedHorse.length; j++)
                differencesOfOrderedHorse[i][j] = performanceOfOrderedHorse[i] - performanceOfOrderedHorse[j];
        int pause = 600;
        boolean canPause = showBets && HorjeRace.display != null && !HorjeRace.display.getFastComputerPlayCheck().isSelected();
        int money = player.getMoney();
        int maxAmount = (int)(player.getMoney() * player.getMaxMoneyBet() / 100.0);
        int minAmount = Math.max((int)(player.getMoney() * Player.MIN_BET_AMOUNT),
                (int)(player.getMoney() * player.getMinMoneyBet() / 100.0));
        boolean[] availableBets = HorjeRace.raceOptions.getBetTypeChecks();
        Bet bet = new Bet();
        player.addBet(bet);
        int amount = UtMath.ranged(roundAmount(UtMath.rnd(minAmount, maxAmount)), minAmount, maxAmount);
        int betType;
        do
            betType = UtMath.rnd(0, Bet.BET_NEW_RECORD);
        while (!availableBets[betType]);
        bet.setBetType(betType);
        if (canPause) pauseAndDisplay(pause);
        int searching = UtMath.rnd(player.getMinSearching(), player.getMaxSearching()); // 50 -> searching 11
        double d = searching / 100.0;
        searching = (int)((searching * d * d * d + searching * 0.2) * 1.0); // 0 - 110 non-linear
        double agression = UtMath.rnd(player.getMinAgression(), player.getMaxAgression()); // 42 -> agression 0.80
        agression = agression * agression / 100;
        agression = (agression + 10) / 1.10;
        agression = 20 / agression; // 0.20 - 2.20 non-linear
        double lowestPayoff = Double.MAX_VALUE;
        Bet lowestPayoffBet = bet.clone();
        // gr.tvarsa.hrace.model.Bet inRiskBet = bet.clone();
        if (betType != Bet.NO_BET && betType != Bet.BET_NEW_RECORD) while (true)
        {
            boolean selectedHorseNumbers[] = new boolean[MAX_RACE_HORSES + 1];
            for (int i = 0; i < Bet.HORSES_BET[betType]; i++)
            {
                Horse horse;
                do
                    horse = getHorse(UtMath.rnd(0, countHorses() - 1));
                while (selectedHorseNumbers[horse.getNumber()]);
                selectedHorseNumbers[horse.getNumber()] = true;
                bet.addHorse(horse);
            }
            int positionOfBetHorse[] = new int[bet.countHorses()];
            for (int i = 0; i < bet.countHorses(); i++)
                positionOfBetHorse[i] = getOrderedHorsePos(orderedHorses, bet.getHorse(i));
            double risk = -1;
            switch (betType)
            {
                case Bet.BET_NEW_RECORD:
                    risk = raceNo + 50.0;
                    break;
                case Bet.BET_WIN:
                    double risk0 = 0 + 10;
                    risk0 = (risk0 * risk0 * risk0 - 900) / 18.0;
                    risk = positionOfBetHorse[0] + 10;
                    risk = (risk * risk * risk - 900) / 18.0;
                    double diff = differencesOfOrderedHorse[positionOfBetHorse[0]][0];
                    risk = risk0 + risk * diff / 1.5;
                    break;
                case Bet.BET_SECOND:
                    break;
                case Bet.BET_THIRD:
                    break;
                case Bet.BET_ONE_TWO:
                    break;
                case Bet.BET_FIRST_TWO:
                    break;
                case Bet.BET_ONE_TWO_THREE:
                    break;
                case Bet.BET_FIRST_THREE:
                    break;
            }
            double payoff = HorjeRace.betResult.getPayoff(bet);
            if (UtMath.inRange(risk, player.getMinRisk(), player.getMaxRisk()))
            {
                // inRiskBet = bet.clone();
            }
            if (payoff < lowestPayoff)
            {
                lowestPayoff = payoff;
                lowestPayoffBet = bet.clone();
            }
            double limit = agression * (Bet.HORSES_BET[betType] * Bet.HORSES_BET[betType] / 1.5 + 3.0 + UtMath.rnd(0.0, 3.0));
            searching--;
            if (payoff < limit) break;
            if (searching < 1)
            {
                player.getBets().remove(bet);
                bet = lowestPayoffBet;
                player.addBet(bet);
                break;
            }
            bet.getHorses().clear();
        }
        if (canPause) pauseAndDisplay(pause);
        bet.setAmountBet(amount);
        player.setMoney(money - amount);
        if (showBets)
        {
            if (canPause)
                pauseAndDisplay(pause);
            else
                pauseAndDisplay(200);
        }
    }

    private void getBets(boolean showBets)
    {
        if (countBettingPlayers() < 1 || HorjeRace.raceOptions.countBetTypeChecks() == 0 || countHorses() < 4) return;
        int CHAR_ESC = KeyEvent.VK_ESCAPE;
        int CHAR_BS = KeyEvent.VK_BACK_SPACE;
        int CHAR_RET = KeyEvent.VK_ENTER;
        HorjeDisplay display = HorjeRace.display;
        boolean placedBet[] = new boolean[players.size()];
        boolean selectedHorseNumbers[];
        int placedBets = 0;
        boolean selected = false;
        int playerNo = 0;
        Bet bet = null;
        Player player = null;
        int playerMoney = 0;
        while (placedBets < players.size())
        {
            selectedHorseNumbers = new boolean[MAX_RACE_HORSES + 1];
            if (!selected)
            {
                if (display.getBetByOrderCheck().isSelected())
                    playerNo = placedBets;
                else
                    do
                        playerNo = UtMath.rnd(0, players.size() - 1);
                    while (placedBet[playerNo]);
                placedBet[playerNo] = true;
                player = players.get(playerNo);
                playerMoney = player.getMoney();
                display.getInfoPanel().setHighlightedPlayer(player);
                if (player.getMoney() < 1)
                {
                    selected = false;
                    placedBets++;
                    continue;
                }
            }
            else
                player.getBets().remove(bet);
            if (player.isComputerPlayer())
            {
                setComputerBet(player, showBets);
                selected = false;
                placedBets++;
                continue;
            }
            else if (!showBets)
            {
                player.getBets().clear();
                selected = false;
                placedBets++;
                continue;
            }
            else
            {
                display.repaint();
                selected = true;
            }
            // bet type
            boolean[] availableBets = HorjeRace.raceOptions.getBetTypeChecks();
            String msg = player.getName() + GAP + SPLIT;
            String keys = "r";
            for (int i = 0; i < Bet.BET_TYPES.length; i++)
                if (availableBets[i])
                {
                    msg += GAP + i + " - " + Bet.BET_TYPES[i];
                    keys += i;
                }
            display.setStatus(msg + GAP + "R - Random");
            int key = UtGui.waitForChars(display, true, keys);
            if (key == 'r')
                do
                    key = UtMath.rnd(0, Bet.BET_NEW_RECORD);
                while (!availableBets[key]);
            else
                key = key - '0';
            bet = new Bet();
            bet.setBetType(key);
            player.addBet(bet);
            display.repaint();
            // horses
            int horsesNeeded = Bet.HORSES_BET[bet.getBetType()];
            if (horsesNeeded > 0)
            {
                boolean escape = false;
                keys = "";
                while (horsesNeeded > 0)
                {
                    msg = player.getName() + GAP + SPLIT + GAP + Bet.BET_TYPES[bet.getBetType()] + GAP + SPLIT + GAP + "Enter "
                            + UtString.plural(horsesNeeded, "horse") + GAP + SPLIT;
                    keys = "r";
                    for (int i = 0; i <= countHorses() - 1; i++)
                    {
                        for (int j = 0; j < countHorses(); j++)
                            if (horses.get(j).getNumber() == i + 1)
                            {
                                if (!selectedHorseNumbers[i + 1])
                                {
                                    keys += i + 1;
                                    msg += GAP + (i + 1);
                                }
                                break;
                            }
                    }
                    display.setStatus(msg + GAP + "R - Random");
                    int horseNo = UtGui.waitForChars(display, true, keys, CHAR_ESC);
                    if (horseNo == CHAR_ESC)
                    {
                        player.setMoney(playerMoney);
                        bet.setAmountBet(0);
                        escape = true;
                        break;
                    }
                    else if (horseNo == 'r')
                    {
                        do
                            horseNo = UtMath.rnd(0, countHorses() - 1);
                        while (selectedHorseNumbers[horses.get(horseNo).getNumber()]);
                        horseNo = horses.get(horseNo).getNumber();
                    }
                    else
                        horseNo = horseNo - '0';
                    selectedHorseNumbers[horseNo] = true;
                    for (int i = 0; i < countHorses(); i++)
                        if (horses.get(i).getNumber() == horseNo)
                        {
                            bet.addHorse(horses.get(i));
                            break;
                        }
                    display.repaint();
                    horsesNeeded--;
                }
                if (escape) continue;
            }
            // amount
            int maxAmount = player.getMoney();
            int minAmount = (int)(player.getMoney() * Player.MIN_BET_AMOUNT);
            if (player.getMoney() == 0) minAmount = 0;
            msg = player.getName() + GAP + SPLIT + GAP + Bet.BET_TYPES[bet.getBetType()] + GAP + SPLIT + GAP + "gr.tvarsa.hrace.model.Bet amount"
                    + GAP + SPLIT + GAP + UtString.commaString(minAmount) + " - " + UtString.commaString(maxAmount) + GAP
                    + "N - min" + GAP + "TFWQ - 10% 15% 20% 25%" + GAP + "HSEI - 50% 75% 80% 90%" + GAP + "X - max" + GAP
                    + "R - rand";
            boolean escape = false;
            int amount = 0;
            bet.setAmountBet(0);
            if (bet.getBetType() != Bet.NO_BET) while (true)
            {
                display.setStatus(msg);
                key = UtGui.waitForChars(display, true, "0123456789ntfwqhseixr", CHAR_ESC, CHAR_BS, CHAR_RET);
                if (key == CHAR_ESC)
                {
                    player.setMoney(playerMoney);
                    bet.setAmountBet(0);
                    escape = true;
                    break;
                }
                else if (key == 'n')
                    amount = minAmount;
                else if (key == 't')
                    amount = (int)(maxAmount * 0.10 + 0.5);
                else if (key == 'f')
                    amount = (int)(maxAmount * 0.15 + 0.5);
                else if (key == 'w')
                    amount = (int)(maxAmount * 0.20 + 0.5);
                else if (key == 'q')
                    amount = (int)(maxAmount * 0.25 + 0.5);
                else if (key == 'h')
                    amount = (int)(maxAmount * 0.50 + 0.5);
                else if (key == 's')
                    amount = (int)(maxAmount * 0.75 + 0.5);
                else if (key == 'e')
                    amount = (int)(maxAmount * 0.80 + 0.5);
                else if (key == 'i')
                    amount = (int)(maxAmount * 0.90 + 0.5);
                else if (key == 'x')
                    amount = maxAmount;
                else if (key == 'r')
                {
                    int newAmount = 0;
                    do
                    {
                        double range = maxAmount - minAmount;
                        double rnd = UtMath.rnd(0.0, 1.0);
                        rnd *= rnd;
                        newAmount = (int)(minAmount + rnd * range);
                        String s = "" + newAmount;
                        int digits = s.length();
                        int keep = Math.max(1, digits - 2);
                        keep = Math.min(keep, 2);
                        s = s.substring(0, keep);
                        while (s.length() < digits)
                            s += "0";
                        newAmount = Integer.parseInt(s);
                    }
                    while (!UtMath.inRange(newAmount, minAmount, maxAmount));
                    amount = newAmount;
                }
                else if (key == CHAR_BS)
                    amount = amount / 10;
                else if (key != CHAR_RET)
                {
                    int newAmount = amount * 10 + key - '0';
                    if (newAmount <= maxAmount) amount = newAmount;
                }
                bet.setAmountBet(amount);
                player.setMoney(playerMoney - amount);
                display.repaint();
                msg = UtString.copyBefore(msg, "RET - ", true).trim();
                if (UtMath.inRange(amount, minAmount, maxAmount))
                {
                    msg += GAP + "RET - OK";
                    if (key == CHAR_RET)
                    {
                        player.setMoney(playerMoney - bet.getAmountBet());
                        break;
                    }
                }
            }
            if (escape) continue;
            //
            display.repaint();
            selected = false;
            placedBets++;
        }
        display.repaint();
    }

    public void getComputerBets()
    {
        horses = new ArrayList<>();
        int noOfHorses = 8;
        for (int i = 0; i < noOfHorses; i++)
        {
            Horse horse = new Horse();
            horses.add(horse);
            horse.setNumber(i + 1);
        }
        HorjeRace.betResult.setHorses(horses);
        List<Horse> orderedHorses = HorjeRace.betResult.getHorsesByPerformance();
        if (orderedHorses.size() != horses.size()) return;
        for (int i = 0; i < noOfHorses; i++)
        {
            // System.out.print((i+1)+" = " + gr.tvarsa.hrace.utility.UtGui.roundStr(horses.get(i).getPerformance(),1)+" ");
        }
        System.out.println();
        for (int i = 0; i < noOfHorses; i++)
        {
            // System.out.print(orderedHorses.get(i).getNumber()+" = "+
            // gr.tvarsa.hrace.utility.UtGui.roundStr(orderedHorses.get(i).getPerformance(),1)+" ");
        }
        System.out.println();
        double performanceOfOrderedHorse[] = new double[countHorses()];
        double differencesOfOrderedHorse[][] = new double[performanceOfOrderedHorse.length][performanceOfOrderedHorse.length];
        for (int i = 0; i < countHorses(); i++)
            performanceOfOrderedHorse[i] = orderedHorses.get(i).getPerformance();
        for (int i = 0; i < performanceOfOrderedHorse.length; i++)
            for (int j = 0; j < performanceOfOrderedHorse.length; j++)
                differencesOfOrderedHorse[i][j] = performanceOfOrderedHorse[i] - performanceOfOrderedHorse[j];
        // int money = 1000;
        //
        // int minMoneyBet = 0;
        // int maxMoneyBet = 20;
        int minSearching = 50;
        int maxSearching = 70;
        int minAgression = 42;
        int maxAgression = 65;
        int minRisk = 0;
        int maxRisk = 40;
        //
        // int maxAmount = (int)(money * maxMoneyBet / 100.0);
        // int minAmount = Math.max((int)(money * gr.tvarsa.hrace.model.Player.MIN_BET_AMOUNT), (int)(money * minMoneyBet / 100.0));
        int searching = UtMath.rnd(minSearching, maxSearching); // 50 -> searching 11
        double d = searching / 100.0;
        searching = (int)((searching * d * d * d + searching * 0.2) * 1.0); // 0 - 110 non-linear
        double agression = UtMath.rnd(minAgression, maxAgression); // 42 -> agression 0.80
        agression = agression * agression / 100;
        agression = (agression + 10) / 1.10;
        agression = 20 / agression; // 0.20 - 2.20 non-linear
        double lowestPayoff = Double.MAX_VALUE;
        for (int betType = 0; betType <= Bet.BET_NEW_RECORD; betType++)
        {
            for (int h = 0; h < noOfHorses; h++)
            {
                Bet bet = new Bet();
                bet.setBetType(Bet.BET_WIN);
                // gr.tvarsa.hrace.model.Bet lowestPayoffBet = bet.clone();
                // gr.tvarsa.hrace.model.Bet inRiskBet = bet.clone();
                // int amount = gr.tvarsa.hrace.utility.UtGui.ranged(roundAmount(gr.tvarsa.hrace.utility.UtGui.rnd(minAmount, maxAmount)), minAmount, maxAmount);
                if (betType == Bet.NO_BET) continue;
                // System.out.println("\nbetType = " + gr.tvarsa.hrace.model.Bet.BET_TYPES[betType]);
                // while (true)

                // boolean selectedHorseNumbers[] = new boolean[MAX_RACE_HORSES + 1];
                Horse horse = null;
                // for (int i = 0; i < gr.tvarsa.hrace.model.Bet.HORSES_BET[betType]; i++)
                // {
                // do
                // horse = getHorse(gr.tvarsa.hrace.utility.UtGui.rnd(0, countHorses() - 1));
                horse = orderedHorses.get(h);
                // while (selectedHorseNumbers[horse.getNumber()]);
                // selectedHorseNumbers[horse.getNumber()] = true;
                bet.addHorse(horse);
                // }
                int positionOfBetHorse[] = new int[bet.countHorses()];
                for (int i = 0; i < bet.countHorses(); i++)
                    positionOfBetHorse[i] = getOrderedHorsePos(orderedHorses, bet.getHorse(i));
                double risk = -1;
                switch (betType)
                {
                    case Bet.BET_NEW_RECORD:
                        risk = raceNo + 50.0;
                        break;
                    case Bet.BET_WIN:
                        double a = 5.0;
                        double c = 85;
                        double risk0 = 0 + 10;
                        risk0 = (risk0 * risk0 - c) / a;
                        risk = positionOfBetHorse[0] + 10;
                        risk = (risk * risk - c) / a;
                        double diff = differencesOfOrderedHorse[0][positionOfBetHorse[0]];
                        risk = risk0 + risk * diff / 2.6;
                        System.out.println(
                                UtString.justifyRight("" + HorjeRace.betResult.getPayoff(Bet.BET_WIN, bet.getHorse(0)), 4)
                                        + "   "/* +gr.tvarsa.hrace.utility.UtString.roundStr(horse.getPerformance(), 1)+"  " */
                                        + UtString.roundStr(risk, 2));
                        break;
                    case Bet.BET_SECOND:
                        break;
                    case Bet.BET_THIRD:
                        break;
                    case Bet.BET_ONE_TWO:
                        break;
                    case Bet.BET_FIRST_TWO:
                        break;
                    case Bet.BET_ONE_TWO_THREE:
                        break;
                    case Bet.BET_FIRST_THREE:
                        break;
                }
                double payoff = HorjeRace.betResult.getPayoff(bet);
                if (UtMath.inRange(risk, minRisk, maxRisk))
                {
                    // inRiskBet = bet.clone();
                }
                if (payoff < lowestPayoff)
                {
                    lowestPayoff = payoff;
                    // lowestPayoffBet = bet.clone();
                }
                // double limit = agression
                // * (gr.tvarsa.hrace.model.Bet.HORSES_BET[betType] * gr.tvarsa.hrace.model.Bet.HORSES_BET[betType] / 1.5 + 3.0 + Functions.rnd(0.0, 3.0));
                // searching--;
                // if (payoff < limit) break;
                // if (searching < 1)
                // {
                // bet = lowestPayoffBet;
                // break;
                // }
                // bet.getHorses().clear();
            }
            // for (int i = 0; i < bet.getHorses().size(); i++)
            // System.out.print(bet.getHorse(i).getNumber() + " ");
            // System.out.println();
        }
        // print bets
    }

}

package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.gui.CameraPanel;
import gr.tvarsa.hrace.gui.InfoPanel;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

public class ReplayControl extends JFrame
{
    public static final int ACTION_STOP = 0;
    public static final int ACTION_MOVE_ONE = 1;
    public static final int ACTION_MOVE_ONE_BACK = 2;
    public static final int ACTION_MOVE_MANY = 3;
    public static final int ACTION_MOVE_MANY_BACK = 4;
    public static final int ACTION_GO_TO_START = 5;
    public static final int ACTION_GO_TO_END = 6;
    public static final int ACTION_GO_TO_ABSOLUTE = 7;

    private JPanel mainPanel = null;
    private JSlider timeSlider = null;
    private JSlider speedSlider = null;
    private JButton stepBackwardButton = null;
    private JToggleButton fastBackwardButton = null;
    private JButton pauseButton = null;
    private JToggleButton playButton = null;
    private JToggleButton playBackwardButton = null;
    private JButton stepForwardButton = null;
    private JToggleButton fastForwardButton = null;
    private JButton toEndButton = null;
    private JButton toStartButton = null;
    private JLabel timeLabel = null;
    private JLabel speedLabel = null;
    private JLabel timeLabel2 = null;
    private JLabel speedLabel2 = null;
    private JCheckBox loopAtEndCheck = null;
    private JCheckBox markFirstHorseCheck = null;
    private JCheckBox markNextToFinishCheck = null;
    private JCheckBox markAllHorsesCheck = null;
    private JButton colorsButton = null;

    private int moveAction = ACTION_STOP;
    private boolean keepMoving = true;
    private boolean manualUpdate = false;
    private boolean userAction = true;

    public ReplayControl()
    {
        super();
        initialize();
    }

    public boolean isUserAction()
    {
        return userAction;
    }

    public int getMoveAction()
    {
        int action = moveAction;
        if (!keepMoving)
        {
            moveAction = ACTION_STOP;
            setToggled(null);
        }
        userAction = false;
        return action;
    }

    public void setUserMoveAction(int action, boolean continuous)
    {
        moveAction = action;
        keepMoving = continuous;
        userAction = true;
    }

    private void initialize()
    {
        this.setTitle("Replay control");
        this.setResizable(false);
        this.setSize(542, 148);
        this.setContentPane(getMainPanel());
    }

    private JPanel getMainPanel()
    {
        if (mainPanel == null)
        {
            speedLabel2 = new JLabel();
            speedLabel2.setText("Speed");
            speedLabel2.setLocation(8, 33);
            speedLabel2.setSize(36, 16);
            timeLabel2 = new JLabel();
            timeLabel2.setText("Time");
            timeLabel2.setLocation(8, 7);
            timeLabel2.setSize(28, 16);
            mainPanel = new JPanel();
            mainPanel.setLayout(null);
            mainPanel.add(getToStartButton());
            mainPanel.add(getFastBackwardButton());
            mainPanel.add(getStepBackwardButton());
            mainPanel.add(getTimeSlider());
            mainPanel.add(getSpeedSlider());
            mainPanel.add(getPauseButton());
            mainPanel.add(getToEndButton());
            mainPanel.add(getFastForwardButton());
            mainPanel.add(getStepForwardButton());
            mainPanel.add(getPlayButton());
            mainPanel.add(getPlayBackwardButton());
            mainPanel.add(getTimeLabel());
            mainPanel.add(getSpeedLabel());
            mainPanel.add(timeLabel2, null);
            mainPanel.add(speedLabel2, null);
            mainPanel.add(getLoopAtEndCheck(), null);
            mainPanel.add(getMarkFirstHorseCheck(), null);
            mainPanel.add(getMarkAllHorsesCheck(), null);
            mainPanel.add(getMarkNextToFinishCheck(), null);
            mainPanel.add(getColorsButton(), null);
        }
        return mainPanel;
    }

    private JSlider getTimeSlider()
    {
        if (timeSlider == null)
        {
            timeSlider = new JSlider();
            timeSlider.setLocation(51, 7);
            timeSlider.setMaximum(10000);
            timeSlider.setMinimum(0);
            timeSlider.setValue(0);
            timeSlider.setSize(416, 19);
            timeSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    int step = getTimeSliderStep();
                    String s;
                    if (step < 0)
                        s = "-";
                    else
                    {
                        s = InfoPanel.doubleToTime(HorjeRace.engine.getHistoryStep(step).getTime());
                        s = UtString.justifyRight(s, 8);
                    }
                    getTimeLabel().setText(s);
                    if (!manualUpdate) setUserMoveAction(ACTION_GO_TO_ABSOLUTE, false);
                }
            });
        }
        return timeSlider;
    }

    public int getTimeSliderStep()
    {
        double step = (double)(getTimeSlider().getValue() - getTimeSlider().getMinimum())
                / (getTimeSlider().getMaximum() - getTimeSlider().getMinimum());
        if (HorjeRace.engine.getHistoryStep() < 0) return -1;
        return (int)Math.round(step * (HorjeRace.engine.countHistorySteps() - 1));
    }

    public void setTimeSliderStep(double step)
    {
        if (HorjeRace.engine.countHistorySteps() < 1)
            step = 0;
        else
            step = step / HorjeRace.engine.countHistorySteps() * (getTimeSlider().getMaximum() - getTimeSlider().getMinimum())
                    + getTimeSlider().getMinimum();
        manualUpdate = true;
        getTimeSlider().setValue((int)Math.round(step));
        manualUpdate = false;
    }

    private JSlider getSpeedSlider()
    {
        if (speedSlider == null)
        {
            speedSlider = new JSlider();
            speedSlider.setLocation(51, 33);
            speedSlider.setMaximum(100);
            speedSlider.setMinimum(1);
            speedSlider.setValue(100);
            speedSlider.setSize(416, 19);
            speedSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getSpeedLabel().setText("x" + UtString.roundStr(getSpeedSliderValue(), 2));
                }
            });
        }
        return speedSlider;
    }

    public double getSpeedSliderValue()
    {
        return getSpeedSlider().getValue() / 100.0;
    }

    public void setSpeedSliderValue(double multiplier)
    {
        multiplier = UtMath.ranged(multiplier, 0.01, 1.0);
        getSpeedSlider().setValue((int)(multiplier * 100));
    }

    public void setToggled(JToggleButton button)
    {
        JToggleButton buttons[] = {fastBackwardButton, playButton, playBackwardButton, fastForwardButton};
        for (int i = 0; i < buttons.length; i++)
            if (buttons[i] != button) buttons[i].setSelected(false);
    }

    private JToggleButton getFastBackwardButton()
    {
        if (fastBackwardButton == null)
        {
            fastBackwardButton = new JToggleButton();
            fastBackwardButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            fastBackwardButton.setText("<<");
            fastBackwardButton.setSize(54, 26);
            fastBackwardButton.setLocation(66, 88);
            fastBackwardButton.setForeground(Color.blue);
            fastBackwardButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (fastBackwardButton.isSelected())
                        setUserMoveAction(ACTION_MOVE_MANY_BACK, true);
                    else
                        setUserMoveAction(ACTION_STOP, true);
                    setToggled(fastBackwardButton);
                }
            });
        }
        return fastBackwardButton;
    }

    private JButton getStepBackwardButton()
    {
        if (stepBackwardButton == null)
        {
            stepBackwardButton = new JButton();
            stepBackwardButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            stepBackwardButton.setText("< II");
            stepBackwardButton.setSize(54, 26);
            stepBackwardButton.setLocation(182, 88);
            stepBackwardButton.setForeground(new Color(0, 192, 0));
            stepBackwardButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setUserMoveAction(ACTION_MOVE_ONE_BACK, false);
                    setToggled(null);
                }
            });
        }
        return stepBackwardButton;
    }

    private JButton getPauseButton()
    {
        if (pauseButton == null)
        {
            pauseButton = new JButton();
            pauseButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            pauseButton.setSelectedIcon(null);
            pauseButton.setSize(54, 26);
            pauseButton.setLocation(240, 88);
            pauseButton.setText("II");
            pauseButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setUserMoveAction(ACTION_STOP, false);
                    setToggled(null);
                }
            });
        }
        return pauseButton;
    }

    private JToggleButton getPlayButton()
    {
        if (playButton == null)
        {
            playButton = new JToggleButton();
            playButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            playButton.setForeground(Color.red);
            playButton.setSize(54, 26);
            playButton.setLocation(356, 88);
            playButton.setText(">");
            playButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (playButton.isSelected())
                        setUserMoveAction(ACTION_MOVE_ONE, true);
                    else
                        setUserMoveAction(ACTION_STOP, true);
                    setToggled(playButton);
                }
            });
        }
        return playButton;
    }

    private JToggleButton getFastForwardButton()
    {
        if (fastForwardButton == null)
        {
            fastForwardButton = new JToggleButton();
            fastForwardButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            fastForwardButton.setForeground(Color.blue);
            fastForwardButton.setSize(54, 26);
            fastForwardButton.setLocation(414, 88);
            fastForwardButton.setText(">>");
            fastForwardButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (fastForwardButton.isSelected())
                        setUserMoveAction(ACTION_MOVE_MANY, true);
                    else
                        setUserMoveAction(ACTION_STOP, true);
                    setToggled(fastForwardButton);
                }
            });
        }
        return fastForwardButton;
    }

    private JButton getStepForwardButton()
    {
        if (stepForwardButton == null)
        {
            stepForwardButton = new JButton();
            stepForwardButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            stepForwardButton.setForeground(new Color(0, 192, 0));
            stepForwardButton.setSize(54, 26);
            stepForwardButton.setLocation(298, 88);
            stepForwardButton.setText("II >");
            stepForwardButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setUserMoveAction(ACTION_MOVE_ONE, false);
                    setToggled(null);
                }
            });
        }
        return stepForwardButton;
    }

    private JButton getToEndButton()
    {
        if (toEndButton == null)
        {
            toEndButton = new JButton();
            toEndButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            toEndButton.setText(">>I");
            toEndButton.setSize(54, 26);
            toEndButton.setLocation(472, 88);
            toEndButton.setForeground(new Color(0, 0, 128));
            toEndButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setUserMoveAction(ACTION_GO_TO_END, false);
                    setToggled(null);
                }
            });
        }
        return toEndButton;
    }

    private JButton getToStartButton()
    {
        if (toStartButton == null)
        {
            toStartButton = new JButton();
            toStartButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            toStartButton.setText("I<<");
            toStartButton.setSize(54, 26);
            toStartButton.setLocation(8, 88);
            toStartButton.setForeground(new Color(0, 0, 128));
            toStartButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    setUserMoveAction(ACTION_GO_TO_START, false);
                    setToggled(null);
                }
            });
        }
        return toStartButton;
    }

    private JToggleButton getPlayBackwardButton()
    {
        if (playBackwardButton == null)
        {
            playBackwardButton = new JToggleButton();
            playBackwardButton.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
            playBackwardButton.setText("<");
            playBackwardButton.setSize(54, 26);
            playBackwardButton.setLocation(124, 88);
            playBackwardButton.setForeground(Color.red);
            playBackwardButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    if (playBackwardButton.isSelected())
                        setUserMoveAction(ACTION_MOVE_ONE_BACK, true);
                    else
                        setUserMoveAction(ACTION_STOP, true);
                    setToggled(playBackwardButton);
                }
            });
        }
        return playBackwardButton;
    }

    private JButton getColorsButton()
    {
        if (colorsButton == null)
        {
            colorsButton = new JButton();
            colorsButton.setText("Colors");
            colorsButton.setSize(72, 22);
            colorsButton.setLocation(454, 58);
            colorsButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    CameraPanel.randomHorseColors();
                }
            });
        }
        return colorsButton;
    }

    private JLabel getTimeLabel()
    {
        if (timeLabel == null)
        {
            timeLabel = new JLabel();
            timeLabel.setText("-");
            timeLabel.setLocation(475, 8);
            timeLabel.setSize(50, 15);
            timeLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
        }
        return timeLabel;
    }

    private JLabel getSpeedLabel()
    {
        if (speedLabel == null)
        {
            speedLabel = new JLabel();
            speedLabel.setText("x1.00");
            speedLabel.setLocation(475, 34);
            speedLabel.setSize(50, 15);
            speedLabel.setFont(new java.awt.Font("Arial", Font.PLAIN, 13));
        }
        return speedLabel;
    }

    public JCheckBox getLoopAtEndCheck()
    {
        if (loopAtEndCheck == null)
        {
            loopAtEndCheck = new JCheckBox();
            loopAtEndCheck.setSize(87, 24);
            loopAtEndCheck.setText("loop at end");
            loopAtEndCheck.setLocation(4, 57);
        }
        return loopAtEndCheck;
    }

    public JCheckBox getMarkFirstHorseCheck()
    {
        if (markFirstHorseCheck == null)
        {
            markFirstHorseCheck = new JCheckBox();
            markFirstHorseCheck.setText("mark first horse");
            markFirstHorseCheck.setLocation(91, 57);
            markFirstHorseCheck.setSize(120, 24);
        }
        return markFirstHorseCheck;
    }

    public JCheckBox getMarkNextToFinishCheck()
    {
        if (markNextToFinishCheck == null)
        {
            markNextToFinishCheck = new JCheckBox();
            markNextToFinishCheck.setText("mark next to finish");
            markNextToFinishCheck.setLocation(320, 57);
            markNextToFinishCheck.setSize(132, 24);
        }
        return markNextToFinishCheck;
    }

    public JCheckBox getMarkAllHorsesCheck()
    {
        if (markAllHorsesCheck == null)
        {
            markAllHorsesCheck = new JCheckBox();
            markAllHorsesCheck.setText("mark all horses");
            markAllHorsesCheck.setLocation(208, 57);
            markAllHorsesCheck.setSize(115, 24);
        }
        return markAllHorsesCheck;
    }
}

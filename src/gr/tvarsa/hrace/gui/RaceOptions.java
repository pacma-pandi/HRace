package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.model.Bet;
import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.utility.UtMath;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;

public class RaceOptions extends JFrame
{

    private JButton cancelButton = null;
    private JButton clearBetTypesButton = null;
    private JButton clearHorseLevelsButton = null;
    private JButton clearRaceDistanceButton = null;
    private JButton closeButton = null;
    private JButton defaultButton = null;
    private JButton invertBetTypesButton = null;
    private JButton invertHorseLevelsButton = null;
    private JButton invertRaceDistanceButton = null;
    private JCheckBox linearCheck = null;
    private JCheckBox[] betTypeChecks = new JCheckBox[Bet.BET_NEW_RECORD - Bet.NO_BET + 1];
    private JCheckBox[] horseLevelChecks = new JCheckBox[Engine.MAX_HORSE_LEVEL - Engine.MIN_HORSE_LEVEL + 1];
    private JCheckBox[] raceDistanceChecks = new JCheckBox[Engine.MAX_RACE_FURLONGS - Engine.MIN_RACE_FURLONGS + 1];
    private JLabel betTypesLabel = null;
    private JLabel driftRateLabel = null;
    private JLabel furlongLengthLabel = null;
    private JLabel gallupRateLabel = null;
    private JLabel horseLevelsLabel = null;
    private JLabel injuryRateLabel = null;
    private JLabel jLabel12 = null;
    private JLabel jLabel13 = null;
    private JLabel jLabel14 = null;
    private JLabel jLabel15 = null;
    private JLabel jLabel16 = null;
    private JLabel jLabel17 = null;
    private JLabel jLabel18 = null;
    private JLabel jLabel19 = null;
    private JLabel jLabel20 = null;
    private JLabel jLabel21 = null;
    private JLabel jLabel22 = null;
    private JLabel loopPauseLabel = null;
    private JLabel maxDelayLabel = null;
    private JLabel maxHorsesLabel = null;
    private JLabel minDelayLabel = null;
    private JLabel minHorsesLabel = null;
    private JLabel raceDistanceLabel = null;
    private JLabel randomnessLabel = null;
    private JLabel timeFactorLabel = null;
    private JPanel jPanel = null;
    private JPanel jPanel1 = null;
    private JPanel jPanel2 = null;
    private JPanel jPanel3 = null;
    private JPanel jPanel4 = null;
    private JPanel jPanel5 = null;
    private JPanel jPanel6 = null;
    private JPanel mainPanel = null;
    private JSlider furlongLengthSlider = null;
    private JSlider gallupRateSlider = null;
    private JSlider driftRateSlider = null;
    private JSlider injuryRateSlider = null;
    private JSlider loopPauseSlider = null;
    private JSlider maxDelaySlider = null;
    private JSlider maxHorsesSlider = null;
    private JSlider minDelaySlider = null;
    private JSlider minHorsesSlider = null;
    private JSlider randomnessSlider = null;
    private JSlider timeFactorSlider = null;

    private boolean lockSliders = true;

    public RaceOptions()
    {
        super();
        for (int i = 0; i <= Engine.MAX_HORSE_LEVEL - Engine.MIN_HORSE_LEVEL; i++)
        {
            horseLevelChecks[i] = new JCheckBox("" + (i + Engine.MIN_HORSE_LEVEL));
            horseLevelChecks[i].setSize(40, 19);
            horseLevelChecks[i].setSelected(true);
        }
        for (int i = 0; i <= Engine.MAX_RACE_FURLONGS - Engine.MIN_RACE_FURLONGS; i++)
        {
            raceDistanceChecks[i] = new JCheckBox("" + (i + Engine.MIN_RACE_FURLONGS));
            raceDistanceChecks[i].setSize(40, 19);
            raceDistanceChecks[i].setSelected(true);
        }
        for (int i = 0; i <= Bet.BET_NEW_RECORD - Bet.NO_BET; i++)
        {
            betTypeChecks[i] = new JCheckBox(Bet.BET_TYPES[i + Bet.NO_BET]);
            betTypeChecks[i].setSize(66, 19);
            betTypeChecks[i].setSelected(true);
        }
        initialize();
        setSliders(getMainPanel());
    }

    public void setSliders(JPanel panel)
    {
        for (int i = 0; i < panel.getComponentCount(); i++)
        {
            Component component = panel.getComponents()[i];
            if (component instanceof JPanel)
                setSliders((JPanel)component);
            else if (component instanceof JSlider)
            {
                lockSliders = false;
                JSlider slider = (JSlider)component;
                int value = slider.getValue();
                slider.setValue(slider.getMinimum());
                slider.setValue(slider.getMaximum());
                slider.setValue(value);
                lockSliders = true;
            }
        }
    }

    private void initialize()
    {
        this.setSize(612, 549);
        this.setContentPane(getMainPanel());
        this.setResizable(false);
        this.setTitle("Race options");
    }

    private void copyContents(JPanel fromPanel, JPanel toPanel)
    {
        for (int i = 0; i < fromPanel.getComponentCount(); i++)
        {
            Component fromComponent = fromPanel.getComponents()[i];
            Component toComponent = toPanel.getComponents()[i];
            if (fromComponent instanceof JPanel)
                copyContents((JPanel)fromComponent, (JPanel)toComponent);
            else if (fromComponent instanceof JSlider)
                ((JSlider)toComponent).setValue(((JSlider)fromComponent).getValue());
            else if (fromComponent instanceof JCheckBox)
                ((JCheckBox)toComponent).setSelected(((JCheckBox)fromComponent).isSelected());
        }
    }

    public void copyOptionsFrom(RaceOptions options)
    {
        copyContents(options.getMainPanel(), getMainPanel());
    }

    private JPanel getMainPanel()
    {
        if (mainPanel == null)
        {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.ipadx = 605;
            gridBagConstraints11.ipady = 67;
            gridBagConstraints11.gridy = 2;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.ipadx = 605;
            gridBagConstraints5.ipady = 44;
            gridBagConstraints5.gridy = 6;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.ipadx = 605;
            gridBagConstraints4.ipady = 112;
            gridBagConstraints4.gridy = 5;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.ipadx = 605;
            gridBagConstraints3.ipady = 92;
            gridBagConstraints3.gridy = 4;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.ipadx = 605;
            gridBagConstraints2.ipady = 67;
            gridBagConstraints2.gridy = 3;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.ipadx = 605;
            gridBagConstraints1.ipady = 67;
            gridBagConstraints1.gridy = 1;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.ipadx = 605;
            gridBagConstraints.ipady = 67;
            gridBagConstraints.gridy = 0;
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            mainPanel.add(getJPanel2(), gridBagConstraints);
            mainPanel.add(getJPanel(), gridBagConstraints1);
            mainPanel.add(getJPanel5(), gridBagConstraints11);
            mainPanel.add(getJPanel4(), gridBagConstraints2);
            mainPanel.add(getJPanel1(), gridBagConstraints3);
            mainPanel.add(getJPanel6(), gridBagConstraints4);
            mainPanel.add(getJPanel3(), gridBagConstraints5);
        }
        return mainPanel;
    }

    public boolean[] getHorseLevelChecks()
    {
        boolean checks[] = new boolean[horseLevelChecks.length];
        boolean found = false;
        for (int i = 0; i < horseLevelChecks.length; i++)
        {
            checks[i] = horseLevelChecks[i].isSelected();
            if (checks[i]) found = true;
        }
        if (!found) for (int i = 0; i < checks.length; i++)
            checks[i] = true;
        return checks;
    }

    public void setHorseLevelRange(int minLevel, int maxLevel)
    {
        for (int i = 0; i < horseLevelChecks.length; i++)
            horseLevelChecks[i].setSelected(UtMath.inRange(i + Engine.MIN_HORSE_LEVEL, minLevel, maxLevel));
    }

    public boolean[] getRaceDistanceChecks()
    {
        boolean checks[] = new boolean[raceDistanceChecks.length];
        boolean found = false;
        for (int i = 0; i < raceDistanceChecks.length; i++)
        {
            checks[i] = raceDistanceChecks[i].isSelected();
            if (checks[i]) found = true;
        }
        if (!found) for (int i = 0; i < checks.length; i++)
            checks[i] = true;
        return checks;
    }

    public void setRaceDistanceRange(int minDistance, int maxDistance)
    {
        for (int i = 0; i < raceDistanceChecks.length; i++)
            raceDistanceChecks[i].setSelected(UtMath.inRange(i + Engine.MIN_RACE_FURLONGS, minDistance, maxDistance));
    }

    public boolean[] getBetTypeChecks()
    {
        boolean checks[] = new boolean[betTypeChecks.length];
        boolean found = false;
        for (int i = 0; i < betTypeChecks.length; i++)
        {
            checks[i] = betTypeChecks[i].isSelected();
            if (checks[i]) found = true;
        }
        if (!found) for (int i = 0; i < checks.length; i++)
            checks[i] = true;
        return checks;
    }

    public int countBetTypeChecks()
    {
        int found = 0;
        for (int i = 0; i < betTypeChecks.length; i++)
            if (betTypeChecks[i].isSelected()) found++;
        return found;
    }

    private JLabel getFurlongLengthLabel()
    {
        if (furlongLengthLabel == null)
        {
            furlongLengthLabel = new JLabel();
            furlongLengthLabel.setText("furlong length");
            furlongLengthLabel.setBounds(new Rectangle(495, 10, 92, 16));
        }
        return furlongLengthLabel;
    }

    private JLabel getTimeFactorLabel()
    {
        if (timeFactorLabel == null)
        {
            timeFactorLabel = new JLabel();
            timeFactorLabel.setText("time factor");
            timeFactorLabel.setBounds(new Rectangle(495, 35, 62, 16));
        }
        return timeFactorLabel;
    }

    private JLabel getGallupRateLabel()
    {
        if (gallupRateLabel == null)
        {
            gallupRateLabel = new JLabel();
            gallupRateLabel.setText("gallup rate");
            gallupRateLabel.setBounds(new Rectangle(495, 60, 60, 16));
        }
        return gallupRateLabel;
    }

    private JLabel getMaxHorsesLabel()
    {
        if (maxHorsesLabel == null)
        {
            maxHorsesLabel = new JLabel();
            maxHorsesLabel.setText("max horses");
            maxHorsesLabel.setBounds(new Rectangle(495, 35, 71, 16));
        }
        return maxHorsesLabel;
    }

    private JLabel getMinHorsesLabel()
    {
        if (minHorsesLabel == null)
        {
            minHorsesLabel = new JLabel();
            minHorsesLabel.setText("min horses");
            minHorsesLabel.setBounds(new Rectangle(495, 10, 64, 16));
        }
        return minHorsesLabel;
    }

    private JLabel getRaceDistanceLabel()
    {
        if (minDelayLabel == null)
        {
            raceDistanceLabel = new JLabel();
            raceDistanceLabel.setText("Race distance");
            raceDistanceLabel.setBounds(new Rectangle(10, 35, 81, 16));
        }
        return raceDistanceLabel;
    }

    private JLabel getHorseLevelsLabel()
    {
        if (horseLevelsLabel == null)
        {
            horseLevelsLabel = new JLabel();
            horseLevelsLabel.setText("gr.tvarsa.hrace.model.Horse levels");
            horseLevelsLabel.setBounds(new Rectangle(10, 10, 70, 16));
        }
        return horseLevelsLabel;
    }

    private JLabel getLoopPauseLabel()
    {
        if (loopPauseLabel == null)
        {
            loopPauseLabel = new JLabel();
            loopPauseLabel.setText("loop pause");
            loopPauseLabel.setBounds(new Rectangle(495, 85, 62, 16));
        }
        return loopPauseLabel;
    }

    private JLabel getMaxDelayLabel()
    {
        if (maxDelayLabel == null)
        {
            maxDelayLabel = new JLabel();
            maxDelayLabel.setText("max delay");
            maxDelayLabel.setBounds(new Rectangle(495, 35, 58, 16));
        }
        return maxDelayLabel;
    }

    private JLabel getRandomnessLabel()
    {
        if (randomnessLabel == null)
        {
            randomnessLabel = new JLabel();
            randomnessLabel.setText("randomness");
            randomnessLabel.setBounds(new Rectangle(495, 10, 72, 16));
        }
        return randomnessLabel;
    }

    private JLabel getInjuryRateLabel()
    {
        if (injuryRateLabel == null)
        {
            injuryRateLabel = new JLabel();
            injuryRateLabel.setText("injury rate");
            injuryRateLabel.setBounds(new Rectangle(495, 35, 57, 16));
        }
        return injuryRateLabel;
    }

    private JLabel getDriftRateLabel()
    {
        if (driftRateLabel == null)
        {
            driftRateLabel = new JLabel();
            driftRateLabel.setText("drift rate");
            driftRateLabel.setBounds(new Rectangle(495, 60, 57, 16));
        }
        return driftRateLabel;
    }

    private JLabel getMinDelayLabel()
    {
        if (minDelayLabel == null)
        {
            minDelayLabel = new JLabel();
            minDelayLabel.setText("min delay");
            minDelayLabel.setBounds(new Rectangle(495, 10, 54, 16));
        }
        return minDelayLabel;
    }

    private JSlider getMinDelaySlider()
    {
        if (minDelaySlider == null)
        {
            minDelaySlider = new JSlider(0, 500, 0);
            minDelaySlider.setLocation(112, 10);
            minDelaySlider.setSize(371, 19);
            minDelaySlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    if (lockSliders && getMaxDelayValue() < getMinDelayValue())
                        getMaxDelaySlider().setValue(getMinDelayValue());
                    getMinDelayLabel().setText(getMinDelayValue() + " ms");
                }
            });
        }
        return minDelaySlider;
    }

    public int getMinDelayValue()
    {
        return getMinDelaySlider().getValue();
    }

    private JSlider getMaxDelaySlider()
    {
        if (maxDelaySlider == null)
        {
            maxDelaySlider = new JSlider(0, 500, 200);
            maxDelaySlider.setLocation(112, 35);
            maxDelaySlider.setSize(371, 19);
            maxDelaySlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    if (lockSliders && getMinDelayValue() > getMaxDelayValue())
                        getMinDelaySlider().setValue(getMaxDelayValue());
                    getMaxDelayLabel().setText(getMaxDelayValue() + " ms");
                }
            });
        }
        return maxDelaySlider;
    }

    public int getMaxDelayValue()
    {
        return getMaxDelaySlider().getValue();
    }

    public JCheckBox getLinearCheck()
    {
        if (linearCheck == null)
        {
            linearCheck = new JCheckBox();
            linearCheck.setText("linear");
            linearCheck.setFont(new Font("Dialog", Font.PLAIN, 12));
            linearCheck.setBounds(new Rectangle(542, 6, 57, 24));
        }
        return linearCheck;
    }

    private JSlider getLoopPauseSlider()
    {
        if (loopPauseSlider == null)
        {
            loopPauseSlider = new JSlider(0, 100, 32);
            loopPauseSlider.setSize(371, 19);
            loopPauseSlider.setLocation(112, 85);
            loopPauseSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getLoopPauseLabel().setText(getLoopPauseValue() + " ms");
                }
            });
        }
        return loopPauseSlider;
    }

    public int getLoopPauseValue()
    {
        return getLoopPauseSlider().getValue();
    }

    public void setLoopPauseValue(int value)
    {
        getLoopPauseSlider().setValue(value);
    }

    private JPanel getJPanel()
    {
        if (jPanel == null)
        {
            jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jPanel.add(getHorseLevelsLabel(), null);
            jPanel.add(getRaceDistanceLabel(), null);
            jPanel.add(getInvertHorseLevelsButton(), null);
            jPanel.add(getClearHorseLevelsButton(), null);
            jPanel.add(getClearRaceDistanceButton(), null);
            jPanel.add(getInvertRaceDistanceButton(), null);
            int left = 112;
            double width = (double)(495 - left - 2 * 10) / Math.max(horseLevelChecks.length, raceDistanceChecks.length);
            for (int i = 0; i < horseLevelChecks.length; i++)
            {
                jPanel.add(horseLevelChecks[i]);
                horseLevelChecks[i].setLocation((int)(left + i * width), 10);
            }
            for (int i = 0; i < raceDistanceChecks.length; i++)
            {
                jPanel.add(raceDistanceChecks[i]);
                raceDistanceChecks[i].setLocation((int)(left + i * width), 35);
            }
        }
        return jPanel;
    }

    private JPanel getJPanel5()
    {
        if (jPanel5 == null)
        {
            betTypesLabel = new JLabel();
            betTypesLabel.setText("Available bet types");
            betTypesLabel.setLocation(10, 11);
            betTypesLabel.setSize(106, 16);
            jPanel5 = new JPanel();
            jPanel5.setLayout(null);
            jPanel5.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jPanel5.add(betTypesLabel, null);
            jPanel5.add(getClearBetTypesButton(), null);
            jPanel5.add(getInvertBetTypesButton(), null);
            int left = 9;
            double width = (double)(610 - left - 2 * 10) / betTypeChecks.length;
            for (int i = 0; i < betTypeChecks.length; i++)
            {
                jPanel5.add(betTypeChecks[i]);
                betTypeChecks[i].setLocation((int)(left + i * width), 37);
            }
        }
        return jPanel5;
    }

    private JPanel getJPanel1()
    {
        if (jPanel1 == null)
        {
            jLabel16 = new JLabel();
            jLabel16.setText("Randomness");
            jLabel16.setBounds(new Rectangle(10, 10, 75, 16));
            jLabel17 = new JLabel();
            jLabel17.setText("Injury rate");
            jLabel17.setBounds(new Rectangle(10, 35, 57, 16));
            jLabel22 = new JLabel();
            jLabel22.setText("Drift rate");
            jLabel22.setBounds(new Rectangle(10, 60, 57, 16));
            jPanel1 = new JPanel();
            jPanel1.setLayout(null);
            jPanel1.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jPanel1.add(getInjuryRateSlider(), null);
            jPanel1.add(getInjuryRateLabel(), null);
            jPanel1.add(getRandomnessSlider(), null);
            jPanel1.add(getRandomnessLabel(), null);
            jPanel1.add(getDriftRateSlider(), null);
            jPanel1.add(getDriftRateLabel(), null);
            jPanel1.add(jLabel16, null);
            jPanel1.add(jLabel17, null);
            jPanel1.add(jLabel22, null);
        }
        return jPanel1;
    }

    private JSlider getInjuryRateSlider()
    {
        if (injuryRateSlider == null)
        {
            injuryRateSlider = new JSlider(0, 100, 10);
            injuryRateSlider.setLocation(112, 35);
            injuryRateSlider.setSize(371, 19);
            injuryRateSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getInjuryRateLabel().setText(getInjuryRateValue() + "%");
                }
            });
        }
        return injuryRateSlider;
    }

    public int getInjuryRateValue()
    {
        return getInjuryRateSlider().getValue();
    }

    public void setInjuryRateValue(int value)
    {
        getInjuryRateSlider().setValue(value);
    }

    private JSlider getDriftRateSlider()
    {
        if (driftRateSlider == null)
        {
            driftRateSlider = new JSlider(0, 100, 33);
            driftRateSlider.setLocation(112, 60);
            driftRateSlider.setSize(371, 19);
            driftRateSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getDriftRateLabel().setText(getDriftRateValue() + "%");
                }
            });
        }
        return driftRateSlider;
    }

    public int getDriftRateValue()
    {
        return getDriftRateSlider().getValue();
    }

    public void setDriftRateValue(int value)
    {
        getDriftRateSlider().setValue(value);
    }

    private JSlider getMaxHorsesSlider()
    {
        if (maxHorsesSlider == null)
        {
            maxHorsesSlider = new JSlider(Engine.MIN_RACE_HORSES, Engine.MAX_RACE_HORSES, 8);
            maxHorsesSlider.setLocation(112, 35);
            maxHorsesSlider.setSize(371, 19);
            maxHorsesSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    if (lockSliders && getMinHorsesValue() > getMaxHorsesValue())
                        getMinHorsesSlider().setValue(getMaxHorsesValue());
                    getMaxHorsesLabel().setText("" + getMaxHorsesValue());
                }
            });
        }
        return maxHorsesSlider;
    }

    public int getMinHorsesValue()
    {
        return getMinHorsesSlider().getValue();
    }

    public void setMinHorsesValue(int value)
    {
        getMinHorsesSlider().setValue(value);
    }

    private JSlider getMinHorsesSlider()
    {
        if (minHorsesSlider == null)
        {
            minHorsesSlider = new JSlider(Engine.MIN_RACE_HORSES, Engine.MAX_RACE_HORSES, 8);
            minHorsesSlider.setSize(371, 19);
            minHorsesSlider.setLocation(112, 10);
            minHorsesSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    if (lockSliders && getMaxHorsesValue() < getMinHorsesValue())
                        getMaxHorsesSlider().setValue(getMinHorsesValue());
                    getMinHorsesLabel().setText("" + getMinHorsesValue());
                }
            });
        }
        return minHorsesSlider;
    }

    public int getMaxHorsesValue()
    {
        return getMaxHorsesSlider().getValue();
    }

    public void setMaxHorsesValue(int value)
    {
        getMaxHorsesSlider().setValue(value);
    }

    private JSlider getGallupRateSlider()
    {
        if (gallupRateSlider == null)
        {
            gallupRateSlider = new JSlider(4, 25, 10);
            gallupRateSlider.setSize(371, 19);
            gallupRateSlider.setLocation(112, 60);
            gallupRateSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getGallupRateLabel().setText(getGallupRateValue() + " m");
                }
            });
        }
        return gallupRateSlider;
    }

    public double getGallupRateValue()
    {
        return getGallupRateSlider().getValue() / 10.0;
    }

    private JSlider getFurlongLengthSlider()
    {
        if (furlongLengthSlider == null)
        {
            furlongLengthSlider = new JSlider(2, 80, 8);
            furlongLengthSlider.setSize(371, 19);
            furlongLengthSlider.setLocation(112, 10);
            furlongLengthSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getFurlongLengthLabel().setText(getFurlongLengthValue() + " m");
                }
            });
        }
        return furlongLengthSlider;
    }

    public int getFurlongLengthValue()
    {
        return getFurlongLengthSlider().getValue() * 5;
    }

    public void setFurlongLengthValue(int value)
    {
        getFurlongLengthSlider().setValue(value / 5);
    }

    private JSlider getTimeFactorSlider()
    {
        if (timeFactorSlider == null)
        {
            timeFactorSlider = new JSlider(1, 30, 10);
            timeFactorSlider.setSize(371, 19);
            timeFactorSlider.setLocation(112, 35);
            timeFactorSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getTimeFactorLabel().setText("x " + getTimeFactorValue());
                }
            });
        }
        return timeFactorSlider;
    }

    public double getTimeFactorValue()
    {
        return getTimeFactorSlider().getValue() / 10.0;
    }

    public void setTimeFactorValue(double value)
    {
        getTimeFactorSlider().setValue((int)(value * 10));
    }

    private JSlider getRandomnessSlider()
    {
        if (randomnessSlider == null)
        {
            randomnessSlider = new JSlider(0, 100, 28);
            randomnessSlider.setSize(371, 19);
            randomnessSlider.setLocation(112, 10);
            randomnessSlider.addChangeListener(new javax.swing.event.ChangeListener()
            {
                public void stateChanged(javax.swing.event.ChangeEvent e)
                {
                    getRandomnessLabel().setText(getRandomnessValue() + "%");
                }
            });
        }
        return randomnessSlider;
    }

    public int getRandomnessValue()
    {
        return getRandomnessSlider().getValue();
    }

    public void setRandomnessValue(int value)
    {
        getRandomnessSlider().setValue(value);
    }

    private JPanel getJPanel2()
    {
        if (jPanel2 == null)
        {
            jLabel13 = new JLabel();
            jLabel13.setText("Max horses");
            jLabel13.setBounds(new Rectangle(10, 35, 67, 16));
            jLabel12 = new JLabel();
            jLabel12.setText("Min horses");
            jLabel12.setBounds(new Rectangle(10, 10, 63, 16));
            jPanel2 = new JPanel();
            jPanel2.setLayout(null);
            jPanel2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jPanel2.add(getMaxHorsesSlider(), null);
            jPanel2.add(getMinHorsesSlider(), null);
            jPanel2.add(getMaxHorsesLabel(), null);
            jPanel2.add(getMinHorsesLabel(), null);
            jPanel2.add(jLabel12, null);
            jPanel2.add(jLabel13, null);
        }
        return jPanel2;
    }

    private JPanel getJPanel3()
    {
        if (jPanel3 == null)
        {
            jPanel3 = new JPanel();
            jPanel3.setLayout(null);
            jPanel3.setBorder(null);
            jPanel3.add(getDefaultButton(), null);
            jPanel3.add(getCancelButton(), null);
            jPanel3.add(getCloseButton(), null);
        }
        return jPanel3;
    }

    private JPanel getJPanel4()
    {
        if (jPanel4 == null)
        {
            jLabel15 = new JLabel();
            jLabel15.setText("Max start delay");
            jLabel15.setBounds(new Rectangle(10, 35, 87, 16));
            jLabel14 = new JLabel();
            jLabel14.setText("Min start delay");
            jLabel14.setBounds(new Rectangle(10, 10, 83, 16));
            jPanel4 = new JPanel();
            jPanel4.setLayout(null);
            jPanel4.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jPanel4.add(getMinDelaySlider(), null);
            jPanel4.add(getMaxDelaySlider(), null);
            jPanel4.add(getMinDelayLabel(), null);
            jPanel4.add(getMaxDelayLabel(), null);
            jPanel4.add(getLinearCheck(), null);
            jPanel4.add(jLabel14, null);
            jPanel4.add(jLabel15, null);
        }
        return jPanel4;
    }

    private JPanel getJPanel6()
    {
        if (jPanel6 == null)
        {
            jLabel21 = new JLabel();
            jLabel21.setText("Loop pause");
            jLabel21.setBounds(new Rectangle(10, 85, 66, 16));
            jLabel20 = new JLabel();
            jLabel20.setText("Gallup rate");
            jLabel20.setBounds(new Rectangle(10, 60, 61, 16));
            jLabel19 = new JLabel();
            jLabel19.setText("Time factor");
            jLabel19.setBounds(new Rectangle(10, 35, 65, 16));
            jLabel18 = new JLabel();
            jLabel18.setText("Furlong length");
            jLabel18.setBounds(new Rectangle(10, 10, 82, 16));
            jPanel6 = new JPanel();
            jPanel6.setLayout(null);
            jPanel6.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jPanel6.add(getLoopPauseSlider(), null);
            jPanel6.add(getLoopPauseLabel(), null);
            jPanel6.add(getTimeFactorSlider(), null);
            jPanel6.add(getFurlongLengthLabel(), null);
            jPanel6.add(getFurlongLengthSlider(), null);
            jPanel6.add(getGallupRateLabel(), null);
            jPanel6.add(getGallupRateSlider(), null);
            jPanel6.add(jLabel18, null);
            jPanel6.add(jLabel19, null);
            jPanel6.add(jLabel20, null);
            jPanel6.add(jLabel21, null);
            jPanel6.add(getTimeFactorLabel(), null);
        }
        return jPanel6;
    }

    private JButton getInvertHorseLevelsButton()
    {
        if (invertHorseLevelsButton == null)
        {
            invertHorseLevelsButton = new JButton();
            invertHorseLevelsButton.setFont(new Font("Dialog", Font.PLAIN, 12));
            invertHorseLevelsButton.setLocation(549, 9);
            invertHorseLevelsButton.setSize(50, 21);
            invertHorseLevelsButton.setText("inv");
            invertHorseLevelsButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    for (int i = 0; i < horseLevelChecks.length; i++)
                        horseLevelChecks[i].setSelected(!horseLevelChecks[i].isSelected());
                }
            });
        }
        return invertHorseLevelsButton;
    }

    private JButton getClearHorseLevelsButton()
    {
        if (clearHorseLevelsButton == null)
        {
            clearHorseLevelsButton = new JButton();
            clearHorseLevelsButton.setFont(new Font("Dialog", Font.PLAIN, 12));
            clearHorseLevelsButton.setLocation(495, 9);
            clearHorseLevelsButton.setSize(50, 21);
            clearHorseLevelsButton.setText("clr");
            clearHorseLevelsButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    for (int i = 0; i < horseLevelChecks.length; i++)
                        horseLevelChecks[i].setSelected(false);
                }
            });
        }
        return clearHorseLevelsButton;
    }

    private JButton getClearRaceDistanceButton()
    {
        if (clearRaceDistanceButton == null)
        {
            clearRaceDistanceButton = new JButton();
            clearRaceDistanceButton.setText("clr");
            clearRaceDistanceButton.setSize(50, 21);
            clearRaceDistanceButton.setFont(new Font("Dialog", Font.PLAIN, 12));
            clearRaceDistanceButton.setLocation(495, 34);
            clearRaceDistanceButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    for (int i = 0; i < raceDistanceChecks.length; i++)
                        raceDistanceChecks[i].setSelected(false);
                }
            });
        }
        return clearRaceDistanceButton;
    }

    private JButton getInvertRaceDistanceButton()
    {
        if (invertRaceDistanceButton == null)
        {
            invertRaceDistanceButton = new JButton();
            invertRaceDistanceButton.setText("inv");
            invertRaceDistanceButton.setSize(50, 21);
            invertRaceDistanceButton.setFont(new Font("Dialog", Font.PLAIN, 12));
            invertRaceDistanceButton.setLocation(549, 34);
            invertRaceDistanceButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    for (int i = 0; i < raceDistanceChecks.length; i++)
                        raceDistanceChecks[i].setSelected(!raceDistanceChecks[i].isSelected());
                }
            });
        }
        return invertRaceDistanceButton;
    }

    private JButton getInvertBetTypesButton()
    {
        if (invertBetTypesButton == null)
        {
            invertBetTypesButton = new JButton();
            invertBetTypesButton.setFont(new Font("Dialog", Font.PLAIN, 12));
            invertBetTypesButton.setLocation(549, 9);
            invertBetTypesButton.setSize(50, 21);
            invertBetTypesButton.setText("inv");
            invertBetTypesButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    for (int i = 0; i < betTypeChecks.length; i++)
                        betTypeChecks[i].setSelected(!betTypeChecks[i].isSelected());
                }
            });
        }
        return invertBetTypesButton;
    }

    private JButton getClearBetTypesButton()
    {
        if (clearBetTypesButton == null)
        {
            clearBetTypesButton = new JButton();
            clearBetTypesButton.setFont(new Font("Dialog", Font.PLAIN, 12));
            clearBetTypesButton.setLocation(495, 9);
            clearBetTypesButton.setSize(50, 21);
            clearBetTypesButton.setText("clr");
            clearBetTypesButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    for (int i = 0; i < betTypeChecks.length; i++)
                        betTypeChecks[i].setSelected(false);
                }
            });
        }
        return clearBetTypesButton;
    }

    private JButton getDefaultButton()
    {
        if (defaultButton == null)
        {
            defaultButton = new JButton();
            defaultButton.setBounds(new Rectangle(160, 10, 91, 26));
            defaultButton.setText("Default");
            defaultButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    RaceOptions.this.copyOptionsFrom(new RaceOptions());
                }
            });
        }
        return defaultButton;
    }

    private JButton getCancelButton()
    {
        if (cancelButton == null)
        {
            cancelButton = new JButton();
            cancelButton.setBounds(new Rectangle(260, 10, 91, 26));
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    RaceOptions.this.copyOptionsFrom(HorjeRace.backupOptions);
                    RaceOptions.this.setVisible(false);
                }
            });
        }
        return cancelButton;
    }

    private JButton getCloseButton()
    {
        if (closeButton == null)
        {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.setSize(new Dimension(91, 26));
            closeButton.setLocation(new Point(360, 10));
            closeButton.addActionListener(new java.awt.event.ActionListener()
            {
                public void actionPerformed(java.awt.event.ActionEvent e)
                {
                    HorjeRace.backupOptions.copyOptionsFrom(RaceOptions.this);
                    RaceOptions.this.setVisible(false);
                }
            });
        }
        return closeButton;
    }
}

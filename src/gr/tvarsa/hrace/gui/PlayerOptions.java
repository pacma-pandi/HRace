package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.model.Player;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PlayerOptions extends JFrame
{
    private static final int SELECTED_ALL = -1;
    private static final int SELECTED_HUMAN = -2;
    private static final int SELECTED_COMPUTER = -3;
    private static final int SELECTED_NONE = -4;

    private JPanel jContentPane = null;
    private JPanel collectionsPanel = null;
    private JPanel dataPanel = null;
    private JRadioButton playerButtons[] = new JRadioButton[Engine.MAX_PLAYERS];
    private ButtonGroup playersGroup = new ButtonGroup(); // @jve:decl-index=0:
    private JPanel choicePanel = null;
    private JPanel playersPanel = null;
    private JPanel buttonsPanel = null;
    private JButton addButton = null;
    private JButton removeButton = null;
    private JRadioButton allPlayersButton = null;
    private JRadioButton humanPlayersButton = null;
    private JRadioButton computerPlayersButton = null;
    private JButton insertButton = null;
    private JLabel nameLabel = null;
    private JLabel moneyLabel = null;
    private JPanel namePanel = null;
    private JTextField nameField = null;
    private JTextField moneyField = null;
    private JCheckBox computerPlayerCheck = null;
    private JPanel agressionPanel = null;
    private JPanel searchingPanel = null;
    private JPanel moneyBetPanel = null;
    private JPanel riskPanel = null;
    private JLabel whoLabel = null;
    private JPanel tmpPanel = null;
    private JLabel textLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JLabel jLabel3 = null;
    private JLabel jLabel4 = null;
    private JLabel jLabel5 = null;
    private JLabel jLabel6 = null;
    private JLabel jLabel7 = null;
    private JLabel jLabel8 = null;
    private JSlider minAgressionSlider = null;
    private JSlider maxAgressionSlider = null;
    private JSlider minSearchingSlider = null;
    private JSlider maxSearchingSlider = null;
    private JSlider minMoneyBetSlider = null;
    private JSlider maxMoneyBetSlider = null;
    private JSlider minRiskSlider = null;
    private JSlider maxRiskSlider = null;
    private JLabel minAgressionLabel = null;
    private JLabel minSearchingLabel = null;
    private JLabel minMoneyBetLabel = null;
    private JLabel minRiskLabel = null;
    private JLabel maxAgressionLabel = null;
    private JLabel maxSearchingLabel = null;
    private JLabel maxMoneyBetLabel = null;
    private JLabel maxRiskLabel = null;

    private boolean updatingComponents = false;
    private Engine engine = HorjeRace.engine;
    private HorjeDisplay display = HorjeRace.display;

    public PlayerOptions()
    {
        super();
        initialize();
        ActionListener listener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                displayPlayerOptions();
                if (getSelectedPlayer() >= 0) focusOnName();
            }
        };
        for (int i = 0; i < playerButtons.length; i++)
        {
            playerButtons[i] = new JRadioButton();
            playerButtons[i].setLocation(8, i * 25 + 5);
            playerButtons[i].setSize(140, 20);
            playerButtons[i].setVisible(true);
            playerButtons[i].addActionListener(listener);
            playersGroup.add(playerButtons[i]);
            getPlayersPanel().add(playerButtons[i]);
        }
        playersGroup.add(getAllPlayersButton());
        playersGroup.add(getHumanPlayersButton());
        playersGroup.add(getComputerPlayersButton());
        getAllPlayersButton().addActionListener(listener);
        getHumanPlayersButton().addActionListener(listener);
        getComputerPlayersButton().addActionListener(listener);
        initSliderValues(getDataPanel());
        displayPlayerOptions();
    }

    private void initialize()
    {
        this.setResizable(false);
        this.setSize(548, 365);
        this.setContentPane(getJContentPane());
        this.setTitle("gr.tvarsa.hrace.model.Player setup");
    }

    private void zeroSliders()
    {
        setMinAgressionValue(0);
        setMaxAgressionValue(0);
        setMinSearchingValue(0);
        setMaxSearchingValue(0);
        setMinMoneyBetValue(0);
        setMaxMoneyBetValue(0);
        setMinRiskValue(0);
        setMaxRiskValue(0);
    }

    private void enablePanel(JPanel sourcePanel, boolean enable, Component... exceptedComponents)
    {
        for (int i = 0; i < sourcePanel.getComponentCount(); i++)
        {
            Component c = sourcePanel.getComponent(i);
            boolean inverted = false;
            for (int j = 0; j < exceptedComponents.length; j++)
                if (exceptedComponents[j] == c)
                {
                    inverted = true;
                    break;
                }
            if (c instanceof JPanel)
                enablePanel((JPanel)c, !inverted && enable || inverted && !enable, exceptedComponents);
            else
                c.setEnabled(!inverted && enable || inverted && !enable);
        }
    }

    private void focusOnName()
    {
        if (!getNameField().requestFocusInWindow()) return;
        getNameField().setSelectionStart(0);
        getNameField().setSelectionEnd(getNameField().getText().length());
    }

    public void displayPlayerOptions()
    {
        updatingComponents = true;
        int selected = getSelectedPlayer();
        String title;
        boolean computer = getCommonComputer();
        if (selected == SELECTED_ALL)
        {
            title = getAllPlayersButton().getText().toLowerCase();
            zeroSliders();
            enablePanel(getDataPanel(), false, getMoneyLabel(), getMoneyField(), getComputerPlayerCheck(), whoLabel);
        }
        else if (selected == SELECTED_HUMAN)
        {
            title = getHumanPlayersButton().getText().toLowerCase();
            zeroSliders();
            enablePanel(getDataPanel(), false, getMoneyLabel(), getMoneyField(), getComputerPlayerCheck(), whoLabel);
            computer = false;
        }
        else if (selected == SELECTED_COMPUTER)
        {
            title = getComputerPlayersButton().getText().toLowerCase();
            boolean found = false;
            for (int i = 0; i < engine.countPlayers(); i++)
                if (engine.getPlayer(i).isComputerPlayer())
                {
                    found = true;
                    break;
                }
            Point d = getCommonValue(getMinAgressionSlider());
            if (!found) d = new Point(0, 0);
            setMinAgressionValue(d.x);
            setMaxAgressionValue(d.y);
            d = getCommonValue(getMinSearchingSlider());
            if (!found) d = new Point(0, 0);
            setMinSearchingValue(d.x);
            setMaxSearchingValue(d.y);
            d = getCommonValue(getMinMoneyBetSlider());
            if (!found) d = new Point(0, 0);
            setMinMoneyBetValue(d.x);
            setMaxMoneyBetValue(d.y);
            d = getCommonValue(getMinRiskSlider());
            if (!found) d = new Point(0, 0);
            setMinRiskValue(d.x);
            setMaxRiskValue(d.y);
            enablePanel(getDataPanel(), true, getNameField(), getNameLabel());
            computer = true;
        }
        else if (selected >= 0)
        {
            Player player = engine.getPlayer(selected);
            getComputerPlayerCheck().setSelected(player.isComputerPlayer());
            if (computerPlayerCheck.isSelected())
                enablePanel(getDataPanel(), true);
            else
                enablePanel(getDataPanel(), false, namePanel, whoLabel);
            getNameField().setText(player.getName());
            getMoneyField().setText(UtString.commaString(player.getMoney()));
            setMinAgressionValue(player.getMinAgression());
            setMaxAgressionValue(player.getMaxAgression());
            setMinSearchingValue(player.getMinSearching());
            setMaxSearchingValue(player.getMaxSearching());
            setMinMoneyBetValue(player.getMinMoneyBet());
            setMaxMoneyBetValue(player.getMaxMoneyBet());
            setMinRiskValue(player.getMinRisk());
            setMaxRiskValue(player.getMaxRisk());
            title = player.getName();
        }
        else
        {
            updatingComponents = false;
            return;
        }
        if (selected < 0)
        {
            int money = getCommonMoney();
            getMoneyField().setText(money >= 0 ? UtString.commaString(money) : "");
            getNameField().setText("");
            getComputerPlayerCheck().setSelected(computer);
        }
        getWhoLabel().setText("Data for " + title);
        updatingComponents = false;
    }

    private List<Player> getSelectedPlayers()
    {
        List<Player> players = new ArrayList<>();
        if (engine == null) return players;
        int selected = getSelectedPlayer();
        for (int i = 0; i < engine.countPlayers(); i++)
        {
            Player player = engine.getPlayer(i);
            if (i == selected || selected == SELECTED_ALL || selected == SELECTED_COMPUTER && player.isComputerPlayer()
                    || selected == SELECTED_HUMAN && !player.isComputerPlayer())
                players.add(player);
        }
        return players;
    }

    private int getCommonMoney()
    {
        List<Player> players = getSelectedPlayers();
        if (players.size() == 0) return -1;
        for (int i = 1; i < players.size(); i++)
            if (players.get(i).getMoney() != players.get(i - 1).getMoney()) return -1;
        return players.get(0).getMoney();
    }

    private boolean getCommonComputer()
    {
        if (engine.countPlayers() == 0) return false;
        for (int i = 0; i < engine.countPlayers(); i++)
            if (!engine.getPlayer(i).isComputerPlayer()) return false;
        return true;
    }

    private Point getCommonValue(JSlider minSlider)
    {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < engine.countPlayers(); i++)
        {
            Player player = engine.getPlayer(i);
            if (!player.isComputerPlayer()) continue;
            int minVal = Integer.MAX_VALUE;
            int maxVal = Integer.MIN_VALUE;
            if (minSlider == getMinAgressionSlider())
            {
                minVal = player.getMinAgression();
                maxVal = player.getMaxAgression();
            }
            else if (minSlider == getMinSearchingSlider())
            {
                minVal = player.getMinSearching();
                maxVal = player.getMaxSearching();
            }
            else if (minSlider == getMinMoneyBetSlider())
            {
                minVal = player.getMinMoneyBet();
                maxVal = player.getMaxMoneyBet();
            }
            else if (minSlider == getMinRiskSlider())
            {
                minVal = player.getMinRisk();
                maxVal = player.getMaxRisk();
            }
            if (min > minVal) min = minVal;
            if (max < maxVal) max = maxVal;
        }
        return new Point(min, max);
    }

    private int getSelectedPlayer()
    {
        if (getAllPlayersButton().isSelected()) return SELECTED_ALL;
        if (getHumanPlayersButton().isSelected()) return SELECTED_HUMAN;
        if (getComputerPlayersButton().isSelected()) return SELECTED_COMPUTER;
        for (int i = 0; i < playerButtons.length; i++)
            if (playerButtons[i].isSelected()) return i;
        return SELECTED_NONE;
    }

    public void setPlayers(List<Player> players)
    {
        for (int i = 0; i < playerButtons.length; i++)
        {
            if (players != null && players.size() > i)
            {
                Player player = players.get(i);
                if (player.getName().trim().length() == 0 && player.isComputerPlayer())
                    player.setName(Player.getRandomName(true, players));
                playerButtons[i].setText(player.getName());
                playerButtons[i].setEnabled(true);
                playerButtons[i].setForeground(player.isComputerPlayer() ? new Color(128, 0, 0) : new Color(0, 0, 128));
            }
            else
            {
                playerButtons[i].setText("");
                playerButtons[i].setEnabled(false);
            }
        }
    }

    private void initSliderValues(JPanel panel)
    {
        for (int i = 0; i < panel.getComponentCount(); i++)
        {
            Component component = panel.getComponents()[i];
            if (component instanceof JPanel)
                initSliderValues((JPanel)component);
            else if (component instanceof JSlider)
            {
                updatingComponents = true;
                JSlider slider = (JSlider)component;
                int value = slider.getValue();
                slider.setValue(slider.getMinimum());
                slider.setValue(slider.getMaximum());
                slider.setValue(value);
                updatingComponents = false;
            }
        }
    }

    private JPanel getJContentPane()
    {
        if (jContentPane == null)
        {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getChoicePanel(), BorderLayout.WEST);
            jContentPane.add(getDataPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    private JPanel getCollectionsPanel()
    {
        if (collectionsPanel == null)
        {
            collectionsPanel = new JPanel();
            collectionsPanel.setLayout(null);
            collectionsPanel.setPreferredSize(new Dimension(157, 80));
            collectionsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            collectionsPanel.add(getHumanPlayersButton(), null);
            collectionsPanel.add(getComputerPlayersButton(), null);
            collectionsPanel.add(getAllPlayersButton(), null);
        }
        return collectionsPanel;
    }

    private JPanel getDataPanel()
    {
        if (dataPanel == null)
        {
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.ipady = 4;
            gridBagConstraints11.gridy = 0;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints4.gridy = 5;
            gridBagConstraints4.ipadx = 385;
            gridBagConstraints4.ipady = 63;
            gridBagConstraints4.gridx = 0;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints3.gridy = 4;
            gridBagConstraints3.ipadx = 385;
            gridBagConstraints3.ipady = 63;
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints2.gridy = 3;
            gridBagConstraints2.ipadx = 385;
            gridBagConstraints2.ipady = 63;
            gridBagConstraints2.gridx = 0;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.ipadx = 385;
            gridBagConstraints1.ipady = 63;
            gridBagConstraints1.gridx = 0;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(0, 0, 0, 0);
            gridBagConstraints.gridy = 1;
            gridBagConstraints.ipadx = 385;
            gridBagConstraints.ipady = 63;
            gridBagConstraints.fill = GridBagConstraints.NONE;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridx = 0;
            dataPanel = new JPanel();
            dataPanel.setLayout(new GridBagLayout());
            dataPanel.setBackground(Color.red);
            dataPanel.add(getNamePanel(), gridBagConstraints);
            dataPanel.add(getAgressionPanel(), gridBagConstraints1);
            dataPanel.add(getSearchingPanel(), gridBagConstraints2);
            dataPanel.add(getMoneyBetPanel(), gridBagConstraints3);
            dataPanel.add(getRiskPanel(), gridBagConstraints4);
            dataPanel.add(getWhoLabel(), gridBagConstraints11);
        }
        return dataPanel;
    }

    private JLabel getMoneyLabel()
    {
        if (moneyLabel == null)
        {
            moneyLabel = new JLabel();
            moneyLabel.setText("Money");
            moneyLabel.setLocation(10, 36);
            moneyLabel.setSize(37, 16);
        }
        return moneyLabel;
    }

    private JLabel getNameLabel()
    {
        if (nameLabel == null)
        {
            nameLabel = new JLabel();
            nameLabel.setText("Name");
            nameLabel.setLocation(10, 10);
            nameLabel.setSize(33, 16);
        }
        return nameLabel;
    }

    private JLabel getWhoLabel()
    {
        if (whoLabel == null)
        {
            whoLabel = new JLabel();
            whoLabel.setText("Data for all players");
            whoLabel.setForeground(Color.white);
            whoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            whoLabel.setPreferredSize(new Dimension(385, 28));
        }
        return whoLabel;
    }

    private JPanel getTmpPanel()
    {
        if (tmpPanel == null)
        {
            textLabel = new JLabel();
            textLabel.setText("gr.tvarsa.hrace.model.Player");
            textLabel.setBackground(Color.blue);
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            textLabel.setPreferredSize(new Dimension(36, 20));
            textLabel.setForeground(Color.white);
            tmpPanel = new JPanel();
            tmpPanel.setLayout(new BorderLayout());
            tmpPanel.setBackground(Color.blue);
            tmpPanel.add(textLabel, BorderLayout.NORTH);
            tmpPanel.add(getCollectionsPanel(), BorderLayout.SOUTH);
        }
        return tmpPanel;
    }

    private JPanel getChoicePanel()
    {
        if (choicePanel == null)
        {
            choicePanel = new JPanel();
            choicePanel.setLayout(new BorderLayout());
            choicePanel.add(getTmpPanel(), BorderLayout.NORTH);
            choicePanel.add(getPlayersPanel(), BorderLayout.CENTER);
            choicePanel.add(getButtonsPanel(), BorderLayout.SOUTH);
        }
        return choicePanel;
    }

    private JPanel getPlayersPanel()
    {
        if (playersPanel == null)
        {
            playersPanel = new JPanel();
            playersPanel.setLayout(null);
            playersPanel.setPreferredSize(new Dimension(0, 0));
            playersPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        }
        return playersPanel;
    }

    private JPanel getButtonsPanel()
    {
        if (buttonsPanel == null)
        {
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout());
            buttonsPanel.setPreferredSize(new Dimension(0, 32));
            buttonsPanel.add(getAddButton(), null);
            buttonsPanel.add(getInsertButton(), null);
            buttonsPanel.add(getRemoveButton(), null);
        }
        return buttonsPanel;
    }

    private JRadioButton getAllPlayersButton()
    {
        if (allPlayersButton == null)
        {
            allPlayersButton = new JRadioButton();
            allPlayersButton.setPreferredSize(new Dimension(84, 20));
            allPlayersButton.setSize(84, 20);
            allPlayersButton.setLocation(8, 5);
            allPlayersButton.setText("All players");
            allPlayersButton.setSelected(true);
        }
        return allPlayersButton;
    }

    private JRadioButton getHumanPlayersButton()
    {
        if (humanPlayersButton == null)
        {
            humanPlayersButton = new JRadioButton();
            humanPlayersButton.setPreferredSize(new Dimension(110, 20));
            humanPlayersButton.setSize(110, 20);
            humanPlayersButton.setLocation(8, 30);
            humanPlayersButton.setForeground(new Color(0, 0, 128));
            humanPlayersButton.setText("Human players");
        }
        return humanPlayersButton;
    }

    private JRadioButton getComputerPlayersButton()
    {
        if (computerPlayersButton == null)
        {
            computerPlayersButton = new JRadioButton();
            computerPlayersButton.setPreferredSize(new Dimension(125, 20));
            computerPlayersButton.setSize(132, 20);
            computerPlayersButton.setLocation(8, 55);
            computerPlayersButton.setForeground(new Color(128, 0, 0));
            computerPlayersButton.setText("Computer players");
        }
        return computerPlayersButton;
    }

    private JButton getRemoveButton()
    {
        if (removeButton == null)
        {
            removeButton = new JButton();
            removeButton.setText("del");
            removeButton.setFont(new Font("Dialog", Font.PLAIN, 9));
            removeButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (!engine.stageForChanges()) return;
                    List<Player> players = engine.getPlayers();
                    int selected = getSelectedPlayer();
                    boolean removed = false;
                    for (int i = players.size() - 1; i >= 0; i--)
                    {
                        boolean computer = players.get(i).isComputerPlayer();
                        if (selected == SELECTED_ALL || selected == SELECTED_HUMAN && !computer
                                || selected == SELECTED_COMPUTER && computer || selected == i)
                        {
                            players.remove(i);
                            removed = true;
                        }
                    }
                    if (removed)
                    {
                        setPlayers(players);
                        display.repaint();
                        if (selected >= 0 && players.size() > 0)
                            playerButtons[Math.max(selected - 1, 0)].setSelected(true);
                        else
                        {
                            if (selected == SELECTED_HUMAN)
                                getHumanPlayersButton().setSelected(true);
                            else if (selected == SELECTED_COMPUTER)
                                getComputerPlayersButton().setSelected(true);
                            else
                                getAllPlayersButton().setSelected(true);
                        }
                        displayPlayerOptions();
                    }
                }

            });
        }
        return removeButton;
    }

    private int createPlayer(boolean computerPlayer, boolean insert)
    {
        if (engine.countPlayers() >= Engine.MAX_PLAYERS || !engine.stageForChanges()) return -1;
        int at = getSelectedPlayer();
        if (at < 0 || !insert) at = engine.countPlayers();
        Player player = new Player("", computerPlayer);
        engine.getPlayers().add(at, player);
        display.repaint();
        setPlayers(engine.getPlayers());
        return at;
    }

    private JButton getAddButton()
    {
        if (addButton == null)
        {
            addButton = new JButton();
            addButton.setText("add");
            addButton.setFont(new Font("Dialog", Font.PLAIN, 9));
            addButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    int selected = getSelectedPlayer();
                    int i = createPlayer(
                            selected == SELECTED_COMPUTER || selected >= 0 && engine.getPlayer(selected).isComputerPlayer(),
                            false);
                    if (i < 0) return;
                    playerButtons[i].setSelected(true);
                    displayPlayerOptions();
                    if (i >= 0) focusOnName();
                }
            });
        }
        return addButton;
    }

    private JButton getInsertButton()
    {
        if (insertButton == null)
        {
            insertButton = new JButton();
            insertButton.setText("ins");
            insertButton.setFont(new Font("Dialog", Font.PLAIN, 9));
            insertButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    int selected = getSelectedPlayer();
                    int i = createPlayer(
                            selected == SELECTED_COMPUTER || selected >= 0 && engine.getPlayer(selected).isComputerPlayer(),
                            true);
                    if (i < 0) return;
                    playerButtons[i].setSelected(true);
                    displayPlayerOptions();
                    if (i >= 0) focusOnName();
                }
            });
        }
        return insertButton;
    }

    private JPanel getNamePanel()
    {
        if (namePanel == null)
        {
            namePanel = new JPanel();
            namePanel.setLayout(null);
            namePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            namePanel.add(getNameLabel(), null);
            namePanel.add(getNameField(), null);
            namePanel.add(getMoneyField(), null);
            namePanel.add(getMoneyLabel(), null);
            namePanel.add(getComputerPlayerCheck(), null);
        }
        return namePanel;
    }

    private JTextField getNameField()
    {
        if (nameField == null)
        {
            nameField = new JTextField();
            nameField.setSize(93, 20);
            nameField.setLocation(58, 8);
            nameField.addCaretListener(new CaretListener()
            {
                public void caretUpdate(CaretEvent e)
                {
                    if (updatingComponents) return;
                    int selected = getSelectedPlayer();
                    if (selected < 0) return;
                    String name = nameField.getText().trim();
                    if (name.length() > 8) name = name.substring(0, 8);
                    if (name.length() == 0) return;
                    engine.getPlayer(selected).setName(name);
                    playerButtons[selected].setText(name);
                    display.repaint();
                }
            });
        }
        return nameField;
    }

    private JTextField getMoneyField()
    {
        if (moneyField == null)
        {
            moneyField = new JTextField();
            moneyField.setText("");
            moneyField.setLocation(58, 34);
            moneyField.setSize(93, 20);
            moneyField.addCaretListener(new CaretListener()
            {
                public void caretUpdate(CaretEvent e)
                {
                    if (updatingComponents) return;
                    String s = moneyField.getText().trim();
                    if (s.length() > 9) s = s.substring(0, 9);
                    int money = UtMath.ranged(UtString.stringToInt(s), 0, 100000);
                    List<Player> players = getSelectedPlayers();
                    for (int i = 0; i < players.size(); i++)
                        players.get(i).setMoney(money);
                    display.repaint();
                }
            });
        }
        return moneyField;
    }

    private JCheckBox getComputerPlayerCheck()
    {
        if (computerPlayerCheck == null)
        {
            computerPlayerCheck = new JCheckBox();
            computerPlayerCheck.setText("Computer player");
            computerPlayerCheck.setSize(119, 16);
            computerPlayerCheck.setLocation(255, 10);
            computerPlayerCheck.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    int selected = getSelectedPlayer();
                    if (selected >= 0)
                    {
                        if (computerPlayerCheck.isSelected())
                            enablePanel(getDataPanel(), true);
                        else
                            enablePanel(getDataPanel(), false, namePanel, whoLabel);
                    }
                    if (updatingComponents) return;
                    List<Player> players = getSelectedPlayers();
                    boolean setComputerPlayer = getComputerPlayerCheck().isSelected();
                    for (int i = 0; i < players.size(); i++)
                    {
                        Player player = players.get(i);
                        if (player.isComputerPlayer() != setComputerPlayer)
                        {
                            int at = -1;
                            for (int j = 0; j < engine.countPlayers(); j++)
                                if (engine.getPlayer(j) == player)
                                {
                                    at = j;
                                    break;
                                }
                            if (at == -1) continue;
                            player = new Player("", setComputerPlayer);
                            engine.getPlayers().add(at, player);
                            engine.getPlayers().remove(at + 1);
                        }
                    }
                    setPlayers(engine.getPlayers());
                    displayPlayerOptions();
                    display.repaint();
                }
            });
        }
        return computerPlayerCheck;
    }

    private JPanel getAgressionPanel()
    {
        if (agressionPanel == null)
        {
            agressionPanel = new JPanel();
            agressionPanel.setLayout(null);
            agressionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jLabel1 = new JLabel();
            jLabel1.setText("Max agression");
            jLabel1.setBounds(new Rectangle(10, 35, 86, 16));
            jLabel2 = new JLabel();
            jLabel2.setText("Min agression");
            jLabel2.setBounds(new Rectangle(10, 10, 82, 16));
            agressionPanel.add(getMaxAgressionSlider(), null);
            agressionPanel.add(getMinAgressionSlider(), null);
            agressionPanel.add(jLabel1, null);
            agressionPanel.add(jLabel2, null);
            agressionPanel.add(getMinAgressionLabel(), null);
            agressionPanel.add(getMaxAgressionLabel(), null);
        }
        return agressionPanel;
    }

    private JPanel getSearchingPanel()
    {
        if (searchingPanel == null)
        {
            searchingPanel = new JPanel();
            searchingPanel.setLayout(null);
            searchingPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jLabel3 = new JLabel();
            jLabel3.setText("Max searching");
            jLabel3.setBounds(new Rectangle(10, 35, 84, 16));
            jLabel4 = new JLabel();
            jLabel4.setText("Min searching");
            jLabel4.setBounds(new Rectangle(10, 10, 84, 16));
            searchingPanel.add(getMaxSearchingSlider(), null);
            searchingPanel.add(getMinSearchingSlider(), null);
            searchingPanel.add(jLabel3, null);
            searchingPanel.add(jLabel4, null);
            searchingPanel.add(getMinSearchingLabel(), null);
            searchingPanel.add(getMaxSearchingLabel(), null);
        }
        return searchingPanel;
    }

    private JPanel getMoneyBetPanel()
    {
        if (moneyBetPanel == null)
        {
            moneyBetPanel = new JPanel();
            moneyBetPanel.setLayout(null);
            moneyBetPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jLabel5 = new JLabel();
            jLabel5.setText("Max bet %");
            jLabel5.setBounds(new Rectangle(10, 35, 60, 16));
            jLabel6 = new JLabel();
            jLabel6.setText("Min bet %");
            jLabel6.setBounds(new Rectangle(10, 10, 56, 16));
            moneyBetPanel.add(getMaxMoneyBetSlider(), null);
            moneyBetPanel.add(getMinMoneyBetSlider(), null);
            moneyBetPanel.add(jLabel5, null);
            moneyBetPanel.add(jLabel6, null);
            moneyBetPanel.add(getMinMoneyBetLabel(), null);
            moneyBetPanel.add(getMaxMoneyBetLabel(), null);
        }
        return moneyBetPanel;
    }

    private JPanel getRiskPanel()
    {
        if (riskPanel == null)
        {
            riskPanel = new JPanel();
            riskPanel.setLayout(null);
            riskPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jLabel7 = new JLabel();
            jLabel7.setText("Max risk");
            jLabel7.setBounds(new Rectangle(10, 35, 52, 16));
            jLabel8 = new JLabel();
            jLabel8.setText("Min risk");
            jLabel8.setBounds(new Rectangle(10, 10, 49, 16));
            riskPanel.add(getMaxRiskSlider(), null);
            riskPanel.add(getMinRiskSlider(), null);
            riskPanel.add(jLabel7, null);
            riskPanel.add(jLabel8, null);
            riskPanel.add(getMinRiskLabel(), null);
            riskPanel.add(getMaxRiskLabel(), null);
        }
        return riskPanel;
    }

    private void updateComputerPlayerBehavior()
    {
        int selected = getSelectedPlayer();
        if (selected < 0 && selected != SELECTED_COMPUTER) return;
        List<Player> players = getSelectedPlayers();
        for (int i = 0; i < players.size(); i++)
        {
            Player player = players.get(i);
            player.setMinAgression(getMinAgressionValue());
            player.setMaxAgression(getMaxAgressionValue());
            player.setMinSearching(getMinSearchingValue());
            player.setMaxSearching(getMaxSearchingValue());
            player.setMinMoneyBet(getMinMoneyBetValue());
            player.setMaxMoneyBet(getMaxMoneyBetValue());
            player.setMinRisk(getMinRiskValue());
            player.setMaxRisk(getMaxRiskValue());
        }
    }

    private JSlider getMaxAgressionSlider()
    {
        if (maxAgressionSlider == null)
        {
            maxAgressionSlider = new JSlider(0, 100, 100);
            maxAgressionSlider.setLocation(98, 35);
            maxAgressionSlider.setSize(249, 19);
            maxAgressionSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMinAgressionValue() > getMaxAgressionValue())
                        getMinAgressionSlider().setValue(getMaxAgressionValue());
                    getMaxAgressionLabel().setText("" + getMaxAgressionValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return maxAgressionSlider;
    }

    public int getMaxAgressionValue()
    {
        return getMaxAgressionSlider().getValue();
    }

    private void setMaxAgressionValue(int value)
    {
        getMaxAgressionSlider().setValue(value);
    }

    private JSlider getMinAgressionSlider()
    {
        if (minAgressionSlider == null)
        {
            minAgressionSlider = new JSlider(0, 100, 0);
            minAgressionSlider.setSize(247, 19);
            minAgressionSlider.setLocation(98, 10);
            minAgressionSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMaxAgressionValue() < getMinAgressionValue())
                        getMaxAgressionSlider().setValue(getMinAgressionValue());
                    getMinAgressionLabel().setText("" + getMinAgressionValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return minAgressionSlider;
    }

    public int getMinAgressionValue()
    {
        return getMinAgressionSlider().getValue();
    }

    private void setMinAgressionValue(int value)
    {
        getMinAgressionSlider().setValue(value);
    }

    private JSlider getMaxSearchingSlider()
    {
        if (maxSearchingSlider == null)
        {
            maxSearchingSlider = new JSlider(0, 100, 100);
            maxSearchingSlider.setLocation(98, 35);
            maxSearchingSlider.setSize(249, 19);
            maxSearchingSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMinSearchingValue() > getMaxSearchingValue())
                        getMinSearchingSlider().setValue(getMaxSearchingValue());
                    getMaxSearchingLabel().setText("" + getMaxSearchingValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return maxSearchingSlider;
    }

    public int getMaxSearchingValue()
    {
        return getMaxSearchingSlider().getValue();
    }

    private void setMaxSearchingValue(int value)
    {
        getMaxSearchingSlider().setValue(value);
    }

    private JSlider getMinSearchingSlider()
    {
        if (minSearchingSlider == null)
        {
            minSearchingSlider = new JSlider(0, 100, 0);
            minSearchingSlider.setSize(249, 19);
            minSearchingSlider.setLocation(98, 10);
            minSearchingSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMaxSearchingValue() < getMinSearchingValue())
                        getMaxSearchingSlider().setValue(getMinSearchingValue());
                    getMinSearchingLabel().setText("" + getMinSearchingValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return minSearchingSlider;
    }

    public int getMinSearchingValue()
    {
        return getMinSearchingSlider().getValue();
    }

    private void setMinSearchingValue(int value)
    {
        getMinSearchingSlider().setValue(value);
    }

    private JSlider getMaxMoneyBetSlider()
    {
        if (maxMoneyBetSlider == null)
        {
            maxMoneyBetSlider = new JSlider(0, 100, 100);
            maxMoneyBetSlider.setLocation(98, 35);
            maxMoneyBetSlider.setSize(249, 19);
            maxMoneyBetSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMinMoneyBetValue() > getMaxMoneyBetValue())
                        getMinMoneyBetSlider().setValue(getMaxMoneyBetValue());
                    getMaxMoneyBetLabel().setText("" + getMaxMoneyBetValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return maxMoneyBetSlider;
    }

    public int getMaxMoneyBetValue()
    {
        return getMaxMoneyBetSlider().getValue();
    }

    private void setMaxMoneyBetValue(int value)
    {
        getMaxMoneyBetSlider().setValue(value);
    }

    private JSlider getMinMoneyBetSlider()
    {
        if (minMoneyBetSlider == null)
        {
            minMoneyBetSlider = new JSlider(0, 100, 0);
            minMoneyBetSlider.setSize(249, 19);
            minMoneyBetSlider.setLocation(98, 10);
            minMoneyBetSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMaxMoneyBetValue() < getMinMoneyBetValue())
                        getMaxMoneyBetSlider().setValue(getMinMoneyBetValue());
                    getMinMoneyBetLabel().setText("" + getMinMoneyBetValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return minMoneyBetSlider;
    }

    public int getMinMoneyBetValue()
    {
        return getMinMoneyBetSlider().getValue();
    }

    private void setMinMoneyBetValue(int value)
    {
        getMinMoneyBetSlider().setValue(value);
    }

    private JSlider getMaxRiskSlider()
    {
        if (maxRiskSlider == null)
        {
            maxRiskSlider = new JSlider(0, 100, 100);
            maxRiskSlider.setLocation(98, 35);
            maxRiskSlider.setSize(249, 19);
            maxRiskSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMinRiskValue() > getMaxRiskValue())
                        getMinRiskSlider().setValue(getMaxRiskValue());
                    getMaxRiskLabel().setText("" + getMaxRiskValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return maxRiskSlider;
    }

    public int getMaxRiskValue()
    {
        return getMaxRiskSlider().getValue();
    }

    private void setMaxRiskValue(int value)
    {
        getMaxRiskSlider().setValue(value);
    }

    private JSlider getMinRiskSlider()
    {
        if (minRiskSlider == null)
        {
            minRiskSlider = new JSlider(0, 100, 0);
            minRiskSlider.setSize(249, 19);
            minRiskSlider.setLocation(98, 10);
            minRiskSlider.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    if (!updatingComponents && getMaxRiskValue() < getMinRiskValue())
                        getMaxRiskSlider().setValue(getMinRiskValue());
                    getMinRiskLabel().setText("" + getMinRiskValue());
                    if (!updatingComponents) updateComputerPlayerBehavior();
                }
            });
        }
        return minRiskSlider;
    }

    public int getMinRiskValue()
    {
        return getMinRiskSlider().getValue();
    }

    private void setMinRiskValue(int value)
    {
        getMinRiskSlider().setValue(value);
    }

    private JLabel getMinAgressionLabel()
    {
        if (minAgressionLabel == null)
        {
            minAgressionLabel = new JLabel();
            minAgressionLabel.setText("min");
            minAgressionLabel.setBounds(new Rectangle(350, 10, 30, 16));
        }
        return minAgressionLabel;
    }

    private JLabel getMaxAgressionLabel()
    {
        if (maxAgressionLabel == null)
        {
            maxAgressionLabel = new JLabel();
            maxAgressionLabel.setText("max");
            maxAgressionLabel.setBounds(new Rectangle(350, 35, 30, 16));
        }
        return maxAgressionLabel;
    }

    private JLabel getMinSearchingLabel()
    {
        if (minSearchingLabel == null)
        {
            minSearchingLabel = new JLabel();
            minSearchingLabel.setText("min");
            minSearchingLabel.setBounds(new Rectangle(350, 10, 30, 16));
        }
        return minSearchingLabel;
    }

    private JLabel getMaxSearchingLabel()
    {
        if (maxSearchingLabel == null)
        {
            maxSearchingLabel = new JLabel();
            maxSearchingLabel.setText("max");
            maxSearchingLabel.setBounds(new Rectangle(350, 35, 30, 16));
        }
        return maxSearchingLabel;
    }

    private JLabel getMinMoneyBetLabel()
    {
        if (minMoneyBetLabel == null)
        {
            minMoneyBetLabel = new JLabel();
            minMoneyBetLabel.setText("min");
            minMoneyBetLabel.setBounds(new Rectangle(350, 10, 30, 16));
        }
        return minMoneyBetLabel;
    }

    private JLabel getMaxMoneyBetLabel()
    {
        if (maxMoneyBetLabel == null)
        {
            maxMoneyBetLabel = new JLabel();
            maxMoneyBetLabel.setText("max");
            maxMoneyBetLabel.setBounds(new Rectangle(350, 35, 30, 16));
        }
        return maxMoneyBetLabel;
    }

    private JLabel getMinRiskLabel()
    {
        if (minRiskLabel == null)
        {
            minRiskLabel = new JLabel();
            minRiskLabel.setText("min");
            minRiskLabel.setBounds(new Rectangle(350, 10, 30, 16));
        }
        return minRiskLabel;
    }

    private JLabel getMaxRiskLabel()
    {
        if (maxRiskLabel == null)
        {
            maxRiskLabel = new JLabel();
            maxRiskLabel.setText("max");
            maxRiskLabel.setBounds(new Rectangle(350, 35, 30, 16));
        }
        return maxRiskLabel;
    }
}

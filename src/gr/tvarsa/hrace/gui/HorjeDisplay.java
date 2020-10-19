package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.utility.UtGui;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

public class HorjeDisplay extends JFrame
{
	private JMenuBar mainMenu = null;
	private JMenu fileMenu = null;
	private JMenu playersMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenu = null;
	private JMenuItem raceOptionsMenu = null;
	private JMenuItem setupPlayersMenu = null;
	private JMenuItem aboutMenu = null;
	private JMenuItem debugInfoMenu = null;
	private JPanel screenPanel = null;
	private JPanel statusPanel = null;
	private CameraPanel cameraPanel = null;
	private JLabel statusLabel = null;
	private JCheckBoxMenuItem debugToConsole = null;
	private JCheckBoxMenuItem showHorseRTInfoCheck = null;
	private JCheckBoxMenuItem orderPlayersCheck = null;
	private JCheckBoxMenuItem orderHorsesCheck = null;
	private JCheckBoxMenuItem betByOrderCheck = null;
	private JCheckBoxMenuItem fastComputerPlayCheck = null;
	private JCheckBoxMenuItem subtractMoneyBetCheck = null;
	private JMenu optionsMenu = null;
	private InfoPanel infoPanel = null;

	private TextEditFrame output;
	private Point mousePressedAt = new Point(-1, -1);
	private int keyCode = -1;

	public HorjeDisplay()
	{
		super();
		initialize();
		this.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				keyCode = e.getKeyCode();
			}
		});
		this.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				mousePressedAt = new Point(e.getX(), e.getY());
			}

			public void mouseReleased(MouseEvent e)
			{
				mousePressedAt = new Point(-1, -1);
				setStatus(UtString.copyBefore(getStatus(), "[", true).trim());
			}
			public void mouseExited(MouseEvent e)
			{
				mousePressedAt = new Point(-1, -1);
				setStatus(UtString.copyBefore(getStatus(), "[", true).trim());
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				mousePressedAt = new Point(e.getX(), e.getY());
			}
		});
		output = new TextEditFrame(HorjeDisplay.this, "Debug info", 800, 600, true, new Font("Courier New", Font.PLAIN, 11),
				Color.black, Color.white, new String[] {""}, false, false);
	}

	private void initialize()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{}
		this.setJMenuBar(getMainMenu());
		this.setContentPane(getScreenPanel());
		this.setTitle("Horje Race");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 740);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public int getKeyCode()
	{
		int key = keyCode;
		keyCode = -1;
		return key;
	}

	public Point getMousePressedAt(boolean inCameraPanel)
	{
		if (!inCameraPanel) return mousePressedAt;
		Point screenOrigin = this.getLocationOnScreen();
		Point cameraOrigin = getCameraPanel().getLocationOnScreen();
		return new Point(mousePressedAt.x - cameraOrigin.x + screenOrigin.x, mousePressedAt.y - cameraOrigin.y + screenOrigin.y);
	}

	public void setStatus(String text)
	{
		if (text.length() == 0) text = " ";
		statusLabel.setText(text);
		UtGui.refresh(statusLabel);
	}

	public String getStatus()
	{
		return getStatusLabel().getText();
	}

	public void writeln(String s)
	{
		write(s + "\n");
	}

	public void writeln()
	{
		write("\n");
	}

	public void write(String text)
	{
		if (getDebugToConsole().isSelected()) System.out.print(text);
		output.getTextArea().append(text);
	}

	public void clipDebugOutput()
	{
		String s = output.getText();
		if (s.length() > 32000)
		{
			while (s.length() > 20000)
			{
				int length = s.length();
				s = UtString.copyAfter(s, Engine.NEW_RACE_MARK, true);
				if (s.length() != length)
				{
					int at = s.indexOf(Engine.NEW_RACE_MARK);
					if (at > -1) s = s.substring(at);
				}
				if (s.length() == length) break;
			}
			output.setText(s);
		}
	}

	public void update(Graphics g)
	{
		paint(g);
	}

	public void paint(Graphics g)
	{
		getMainMenu().repaint();
		getCameraPanel().updateScreenContent(getCameraPanel().getGraphics());
		getInfoPanel().updateScreenContent(getInfoPanel().getGraphics());
		getStatusPanel().repaint();
	}

	private JMenuBar getMainMenu()
	{
		if (mainMenu == null)
		{
			mainMenu = new JMenuBar();
			mainMenu.add(getFileMenu());
			mainMenu.add(getPlayersMenu());
			mainMenu.add(getOptionsMenu());
			mainMenu.add(getHelpMenu());
		}
		return mainMenu;
	}

	private JMenu getFileMenu()
	{
		if (fileMenu == null)
		{
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getDebugInfoMenu());
			fileMenu.add(new JSeparator());
			fileMenu.add(getExitMenu());
		}
		return fileMenu;
	}

	private JMenu getPlayersMenu()
	{
		if (playersMenu == null)
		{
			playersMenu = new JMenu();
			playersMenu.setText("Players");
			playersMenu.add(getSetupPlayersMenu());
		}
		return playersMenu;
	}

	private JMenuItem getExitMenu()
	{
		if (exitMenu == null)
		{
			exitMenu = new JMenuItem();
			exitMenu.setText("Exit");
			exitMenu.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			});
		}
		return exitMenu;
	}

	private JMenuItem getDebugInfoMenu()
	{
		if (debugInfoMenu == null)
		{
			debugInfoMenu = new JMenuItem();
			debugInfoMenu.setText("Debug info");
			debugInfoMenu.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					output.getFrame().setVisible(true);
				}
			});
		}
		return debugInfoMenu;
	}

	private JMenu getHelpMenu()
	{
		if (helpMenu == null)
		{
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenu());
		}
		return helpMenu;
	}

	private JMenuItem getAboutMenu()
	{
		if (aboutMenu == null)
		{
			aboutMenu = new JMenuItem();
			aboutMenu.setText("About");
			aboutMenu.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					UtGui.showDialog(HorjeDisplay.this, "Horje Race", "Horje Race 1.0b" + "\n\n(c) 2006 - Thomas Vee\n\n"
							+ "thomas@varsamidis.com\n", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
		return aboutMenu;
	}

	private JPanel getScreenPanel()
	{
		if (screenPanel == null)
		{
			screenPanel = new JPanel();
			screenPanel.setLayout(new BorderLayout());
			screenPanel.add(getInfoPanel(), BorderLayout.NORTH);
			screenPanel.add(getCameraPanel(), java.awt.BorderLayout.CENTER);
			screenPanel.add(getStatusPanel(), java.awt.BorderLayout.SOUTH);
		}
		return screenPanel;
	}

	public CameraPanel getCameraPanel()
	{
		if (cameraPanel == null)
		{
			cameraPanel = new CameraPanel();
		}
		return cameraPanel;
	}

	private JLabel getStatusLabel()
	{
		if (statusLabel == null)
		{
			statusLabel = new JLabel();
			statusLabel.setText("JLabel");
		}
		return statusLabel;
	}

	private JPanel getStatusPanel()
	{
		if (statusPanel == null)
		{
			statusPanel = new JPanel();
			statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			statusPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.SoftBevelBorder.RAISED));
			statusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			statusPanel.add(getStatusLabel());
		}
		return statusPanel;
	}

	public JCheckBoxMenuItem getDebugToConsole()
	{
		if (debugToConsole == null)
		{
			debugToConsole = new JCheckBoxMenuItem();
			debugToConsole.setText("Debug to console");
			debugToConsole.setSelected(false);
			debugToConsole.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (e.getStateChange() != ItemEvent.SELECTED) return;
					if (debugToConsole.isSelected()) System.out.println("\nSeed = " + UtMath.getRandomSeed() + "L");
				}
			});
		}
		return debugToConsole;
	}

	public JCheckBoxMenuItem getOrderPlayersCheck()
	{
		if (orderPlayersCheck == null)
		{
			orderPlayersCheck = new JCheckBoxMenuItem();
			orderPlayersCheck.setText("Order players");
			orderPlayersCheck.setSelected(true);
			orderPlayersCheck.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (e.getStateChange() != ItemEvent.SELECTED) return;
					if (HorjeRace.engine != null) HorjeRace.engine.reorderPlayers(true);
				}
			});
		}
		return orderPlayersCheck;
	}

	public JCheckBoxMenuItem getOrderHorsesCheck()
	{
		if (orderHorsesCheck == null)
		{
			orderHorsesCheck = new JCheckBoxMenuItem();
			orderHorsesCheck.setText("Order horses");
			orderHorsesCheck.setSelected(false);
			orderHorsesCheck.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (e.getStateChange() != ItemEvent.SELECTED) return;
					if (HorjeRace.engine != null) HorjeRace.engine.reorderHorses(orderHorsesCheck.isSelected(), true);
				}
			});
		}
		return orderHorsesCheck;
	}

	public JCheckBoxMenuItem getSubtractMoneyBetCheck()
	{
		if (subtractMoneyBetCheck == null)
		{
			subtractMoneyBetCheck = new JCheckBoxMenuItem();
			subtractMoneyBetCheck.setText("Subtract money bet");
			subtractMoneyBetCheck.setSelected(false);
			subtractMoneyBetCheck.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					if (e.getStateChange() != ItemEvent.SELECTED) return;
					if (HorjeRace.display != null) HorjeRace.display.repaint();
				}
			});
		}
		return subtractMoneyBetCheck;
	}

	public JCheckBoxMenuItem getBetByOrderCheck()
	{
		if (betByOrderCheck == null)
		{
			betByOrderCheck = new JCheckBoxMenuItem();
			betByOrderCheck.setText("gr.tvarsa.hrace.model.Bet by order");
			betByOrderCheck.setSelected(false);
		}
		return betByOrderCheck;
	}

	public JCheckBoxMenuItem getShowHorseRTInfoCheck()
	{
		if (showHorseRTInfoCheck == null)
		{
			showHorseRTInfoCheck = new JCheckBoxMenuItem();
			showHorseRTInfoCheck.setText("Show horse info");
			showHorseRTInfoCheck.setSelected(false);
		}
		return showHorseRTInfoCheck;
	}

	public JCheckBoxMenuItem getFastComputerPlayCheck()
	{
		if (fastComputerPlayCheck == null)
		{
			fastComputerPlayCheck = new JCheckBoxMenuItem();
			fastComputerPlayCheck.setText("Fast computer play");
			fastComputerPlayCheck.setSelected(false);
		}
		return fastComputerPlayCheck;
	}

	private JMenuItem getRaceOptionsMenu()
	{
		if (raceOptionsMenu == null)
		{
			raceOptionsMenu = new JMenuItem();
			raceOptionsMenu.setText("Race options");
			raceOptionsMenu.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if (!HorjeRace.engine.stageForChanges()) return;
					RaceOptions raceOptions = HorjeRace.raceOptions;
					RaceOptions backupOptions = HorjeRace.backupOptions;
					if (raceOptions == null || backupOptions == null) return;
					backupOptions.copyOptionsFrom(raceOptions);
					if (raceOptions.isVisible())
						raceOptions.toFront();
					else
					{
						UtGui.centerOnComponent(HorjeDisplay.this, raceOptions);
						raceOptions.setVisible(true);
					}
				}
			});
		}
		return raceOptionsMenu;
	}

	private JMenuItem getSetupPlayersMenu()
	{
		if (setupPlayersMenu == null)
		{
			setupPlayersMenu = new JMenuItem();
			setupPlayersMenu.setText("Setup players");
			setupPlayersMenu.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					PlayerOptions playerOptions = HorjeRace.playerOptions;
					UtGui.centerOnComponent(HorjeDisplay.this, playerOptions);
					playerOptions.setPlayers(HorjeRace.engine.getPlayers());
					playerOptions.displayPlayerOptions();
					playerOptions.setVisible(true);
				}
			});
		}
		return setupPlayersMenu;
	}

	private JMenu getOptionsMenu()
	{
		if (optionsMenu == null)
		{
			optionsMenu = new JMenu();
			optionsMenu.setText("Options");
			optionsMenu.add(getRaceOptionsMenu());
			optionsMenu.add(new JSeparator());
			optionsMenu.add(getOrderPlayersCheck());
			optionsMenu.add(getBetByOrderCheck());
			optionsMenu.add(getSubtractMoneyBetCheck());
			optionsMenu.add(new JSeparator());
			optionsMenu.add(getOrderHorsesCheck());
			optionsMenu.add(getShowHorseRTInfoCheck());
			optionsMenu.add(new JSeparator());
			optionsMenu.add(getFastComputerPlayCheck());
			optionsMenu.add(new JSeparator());
			optionsMenu.add(getDebugToConsole());
		}
		return optionsMenu;
	}

	public InfoPanel getInfoPanel()
	{
		if (infoPanel == null)
		{
			infoPanel = new InfoPanel();
			infoPanel.setLayout(new GridBagLayout());
			infoPanel.setPreferredSize(new Dimension(0, 167));
		}
		return infoPanel;
	}

}

package gr.tvarsa.hrace.model;

import gr.tvarsa.hrace.app.HorjeRace;
import gr.tvarsa.hrace.utility.UtMath;
import gr.tvarsa.hrace.utility.UtString;

import java.util.ArrayList;
import java.util.List;

public class Player
{
	public static final double MIN_BET_AMOUNT = 0.08;
	public static final int DEFAULT_MONEY = 1000;

	private static String names = "Adam Ade Adel Adrian Al Alan Alex Alice Allen Amanda Amie Amy Andrew Andy Ann Anna "
			+ "Anne Annie Ashley Barry Bart Becky Ben Betsy Betty Bev Bill Bob Brad Brenda Brian Bud Carl Carol Carrie "
			+ "Cathy Cay Charlie Chris Cindy Clair Col Colin Conan Dan Daniel Darren Dave David Dawn Dennis Dick Donna "
			+ "Ed Eddie Edna Elliot Emma Eric Ernie Eve Faye Frank Fred Gary Gaynor George Geoff Geri Gerry Gina Gordon Graham "
			+ "Greg Guy Gwen Harry Helen Hilary Holy Hugh Irene Jack Jade Jake Jamie Jane Jeff Jemma Jen Jenny Jerry Jim Jimmy "
			+ "Jo Joe Joel Joey John Josh Jude Judy Julie Karen Kate Kathy Keith Kelly Kim Lana Larry Laura Lea Lenny Liam "
			+ "Lionel Lisa Liz Loren Luke Mandy Marge Marie Mark Martin Marty Mary Mat Meg Mick Mike Mimi Monica Nancy Nathan"
			+ "Neal Ned Neil Nelly Nick Nicky Nicole Nigel Noel Norm Norman Pat Patsy Paul Paula Perry Pete Peter Phil "
			+ "Rachel Randy Ray Rees Reg Rene Rich Rob Robbie Robert Roger Ron Ronald Ronnie Ross Rupert Russ Ruth Ryan Sam "
			+ "Sammy Sandra Sandy Sarah Sean Sharon Shayne Simon Sophie Stella Steph Steve Steven Stuart Sue Susie "
			+ "Suzan Suzie Tanya Tara Terry Thomas Tim Tina Tom Tony Tory Val Vicki Vicky Vikki Violet Wayne Wendy Will "
			+ "Yvonne";

	private static int playerNo = 1;

	private String name;
	private List<Bet> bets = new ArrayList<>();
	private int money;
	private boolean computerPlayer;
	private int minAgression;
	private int maxAgression;
	private int minSearching;
	private int maxSearching;
	private int minRisk;
	private int maxRisk;
	private int minMoneyBet;
	private int maxMoneyBet;

	public static String getRandomName(boolean computerName, List<Player> existingPlayers)
	{
		String s[] = names.split("\\s+");
		String randomName = "";
		while (true)
		{
			int computerNo = UtMath.rnd(1, 99);
			if (computerName)
				randomName = s[UtMath.rnd(0, s.length - 1)] + computerNo;
			else
			{
				randomName = "gr.tvarsa.hrace.model.Player" + playerNo;
				playerNo = UtMath.warpRange(playerNo + 1, 1, 99);
			}
			if (existingPlayers == null) break;
			boolean found = false;
			for (int i = 0; i < existingPlayers.size(); i++)
				if (existingPlayers.get(i).getName().equalsIgnoreCase(randomName)
						|| computerName && UtString.stringToInt(existingPlayers.get(i).getName()) == computerNo)
				{
					found = true;
					break;
				}
			if (!found) break;
		}
		return randomName;
	}

	public Player(String name, boolean computerPlayer)
	{
		money = DEFAULT_MONEY;
		this.computerPlayer = computerPlayer;
		setName(name);
		setComputerPlayerBehavior(computerPlayer);
	}

	private void setComputerPlayerBehavior(boolean isComputerPlayer)
	{
		if (isComputerPlayer)
		{
			minAgression = 50;
			maxAgression = 70;
			minSearching = 50;
			maxSearching = 70;
			minRisk = 0;
			maxRisk = 0;
			minMoneyBet = 0;
			maxMoneyBet = 20;
		}
		else
		{
			minAgression = 0;
			maxAgression = 0;
			minSearching = 0;
			maxSearching = 0;
			minRisk = 0;
			maxRisk = 0;
			minMoneyBet = 0;
			maxMoneyBet = 0;
		}
	}

	public int countBets()
	{
		return bets.size();
	}

	public Bet getBet(int i)
	{
		if (UtMath.inRange(i, 0, countBets() - 1)) return bets.get(i);
		return null;
	}

	public void addBet(Bet bet)
	{
		bets.add(bet);
	}

	public List<Bet> getBets()
	{
		return bets;
	}

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}

	public int getMaxAgression()
	{
		return maxAgression;
	}

	public void setMaxAgression(int maxAgression)
	{
		this.maxAgression = maxAgression;
	}

	public int getMaxMoneyBet()
	{
		return maxMoneyBet;
	}

	public void setMaxMoneyBet(int maxMoneyBet)
	{
		this.maxMoneyBet = maxMoneyBet;
	}

	public int getMaxRisk()
	{
		return maxRisk;
	}

	public void setMaxRisk(int maxRisk)
	{
		this.maxRisk = maxRisk;
	}

	public int getMaxSearching()
	{
		return maxSearching;
	}

	public void setMaxSearching(int maxSearching)
	{
		this.maxSearching = maxSearching;
	}

	public int getMinAgression()
	{
		return minAgression;
	}

	public void setMinAgression(int minAgression)
	{
		this.minAgression = minAgression;
	}

	public int getMinMoneyBet()
	{
		return minMoneyBet;
	}

	public void setMinMoneyBet(int minMoneyBet)
	{
		this.minMoneyBet = minMoneyBet;
	}

	public int getMinRisk()
	{
		return minRisk;
	}

	public void setMinRisk(int minRisk)
	{
		this.minRisk = minRisk;
	}

	public int getMinSearching()
	{
		return minSearching;
	}

	public void setMinSearching(int minSearching)
	{
		this.minSearching = minSearching;
	}

	public boolean isComputerPlayer()
	{
		return computerPlayer;
	}

	public void setComputerPlayer(boolean computerPlayer)
	{
		this.computerPlayer = computerPlayer;
		setComputerPlayerBehavior(computerPlayer);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		name = name.trim();
		if (name.equals("")) name = getRandomName(computerPlayer, HorjeRace.engine.getPlayers());
		if (name.length() > 8) name = name.substring(0, 8);
		this.name = name;
	}
}

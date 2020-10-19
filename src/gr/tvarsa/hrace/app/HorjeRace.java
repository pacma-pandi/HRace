package gr.tvarsa.hrace.app;

import gr.tvarsa.hrace.dto.BetResult;
import gr.tvarsa.hrace.gui.RaceOptions;
import gr.tvarsa.hrace.gui.ReplayControl;
import gr.tvarsa.hrace.gui.HorjeDisplay;
import gr.tvarsa.hrace.gui.PlayerOptions;
import gr.tvarsa.hrace.model.Engine;
import gr.tvarsa.hrace.model.Player;
import gr.tvarsa.hrace.utility.UtMath;

public class HorjeRace
{
	// do NOT change order - may matter for some statics
	public static final Engine engine = new Engine();
	public static final HorjeDisplay display = new HorjeDisplay();
	public static final BetResult betResult = new BetResult();
	public static final RaceOptions raceOptions = new RaceOptions();
	public static final RaceOptions backupOptions = new RaceOptions();
	public static final PlayerOptions playerOptions = new PlayerOptions();
	public static final ReplayControl replayControl = new ReplayControl();

	public void run()
	{
		display.setStatus("Setting race times...");
		engine.setRandomBestTimes();
		for (int i = 0; i < 0; i++)
			engine.addPlayer(new Player("", UtMath.rnd(0, 1) > 0));
		display.setStatus("");
		engine.setRaceNo(1);
		while (true)
		{
			double result = engine.runRace(raceOptions, true);
			if (result == Engine.QUIT) break;
		}
		System.exit(0);
	}

	public static void main(String[] args)
	{
//		Functions.setRandomSeed(4);
//		engine.getComputerBets();
//		if (true) System.exit(0);
		HorjeRace race = new HorjeRace();
		race.run();
	}
}

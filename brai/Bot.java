package brai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import brai.combat.CombatController;
import brai.scouting.ScoutController;
import brai.strategy.StrategyController;
import brai.ui.ScreenController;
import brai.workers.GasController;
import brai.workers.MiningController;
import brai.workers.Worker;
import brai.workers.WorkerController;
import brai.workers.carpentry.CarpentryController;
import brai.workers.carpentry.RepairController;
import bwapi.Color;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

public class Bot extends DefaultBWListener {
	public static Game game;
	public static Player self;
	public static Player enemy;
	public static BaseLocation main;
	private List<Runnable> controllers;

	@Override
	public void onStart() {
		game = Main.mirror.getGame();
		self = game.self();
		enemy = game.enemies().get(0);

		game.enableFlag(1); // User Input: for debugging only
		game.setLocalSpeed(0);
		game.setFrameSkip(0);

		System.out.println("[BWTA] Reading map...");
		BWTA.readMap();
		System.out.println("[BWTA] Analyzing...");
		BWTA.analyze();
		System.out.println("[BWTA] Done.");
		main = BWTA.getStartLocation(self);

		controllers = new ArrayList<Runnable>();
		controllers.add(StrategyController::update);
		controllers.add(WorkerController::update);
		controllers.add(CarpentryController::update);
		controllers.add(RepairController::update);
		controllers.add(GasController::update);
		controllers.add(MiningController::update);
		controllers.add(ScoutController::update);
		controllers.add(CombatController::update);
		controllers.add(() -> {
			getWorkers().forEach(w -> {
				game.drawTextMap(w.unit.getPosition(), w.job.toString());
				if (w.target != null)
					game.drawLineMap(w.unit.getPosition(), w.target.getPosition(), Color.Red);
			});
		});
		controllers.add(ScreenController::update);
	}

	@Override
	public void onFrame() {
		for (Runnable c : controllers) {
			c.run();
		}
	}

	@Override
	public void onEnd(boolean b) {
		System.out.println("[Meta] Game over: " + (b ? "WON!!!" : "lost"));
		// System.exit(0);
	}

	@Override
	public void onUnitDestroy(Unit u) {
		ScoutController.onUnitDestroy(u);
	}

	public static Stream<Worker> getWorkers() {
		return self.getUnits().stream()
				.filter(u -> u.getType().isWorker() && u.isCompleted() && u.exists())
				.map(u -> Worker.wrap(u));
	}
}
package old;
import java.util.HashMap;
import java.util.Map;

import bwapi.Color;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

public class TestBot3 extends DefaultBWListener {
	private Mirror mirror;
	private Game game;
	private Player self;
	private BaseLocation main;

	public TestBot3(Mirror mirror) {
		this.mirror = mirror;
	}

	@Override
	public void onStart() {
		game = mirror.getGame();
		self = game.self();

		game.enableFlag(1); // User Input: for debugging only
		game.setLocalSpeed(30);

		System.out.println("[BWTA] Reading map...");
		BWTA.readMap();
		System.out.println("[BWTA] Analyzing...");
		BWTA.analyze();
		System.out.println("[BWTA] Done.");
		main = BWTA.getStartLocation(self);
	}

	@Override
	public void onFrame() {
		for (Unit u : self.getUnits()) {
			if (!u.isCompleted() || !u.exists()) {
				continue;
			}
			if (u.getType().isWorker()) {
				if (u.isGatheringMinerals() || u.isCarryingMinerals()) {
					game.drawLineMap(u.getPosition(), u.getTarget().getPosition(), Color.Red);
				}
				// check if it's doing something else
				if (u.isCarryingMinerals()) {
					u.returnCargo();
					continue;
				}
				if (u.isGatheringMinerals() && !u.isMoving()) {
					continue;
				}
				Map<Integer, Integer> fields = new HashMap<Integer, Integer>();
				for (Unit field : main.getMinerals()) {
					fields.put(field.getID(), 0);
				}
				for (Unit otherWorker : self.getUnits()) {
					if (otherWorker.getType().isWorker() && otherWorker.isGatheringMinerals()) {
						fields.put(otherWorker.getTarget().getID(),
								1 + fields.getOrDefault(otherWorker.getTarget(), 0));
					}
				}
				Unit best = null;
				for (int fid : fields.keySet()) {
					Unit field = game.getUnit(fid);
					if (best == null) {
						best = field;
					}
					if (fields.get(fid) < fields.get(best.getID())) {
						best = field;
					} else if (fields.get(fid) == fields.get(best.getID())
							&& field.getDistance(main) < best.getDistance(main)) {
						best = field;
					}
				}
				if (best == null) {
					System.out.println("No minerals?!");
				} else {
					u.gather(best);
				}
				game.drawTextMap(u.getPosition(), "IDLE");
			}
		}
	}

	@Override
	public void onEnd(boolean b) {
		System.out.println("Game over: " + b);
		System.exit(0);
	}
}

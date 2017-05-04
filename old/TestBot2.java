package old;
import java.util.PriorityQueue;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

public class TestBot2 extends DefaultBWListener {
	private Mirror mirror;
	private Game game;
	private Player self;
	private BaseLocation main;
	private PriorityQueue<MineralField> minerals;

	public TestBot2(Mirror mirror) {
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
		
		minerals = new PriorityQueue<MineralField>(main.getMinerals().size(), new MineralField.MFComparator());
		for (Unit mineral : main.getMinerals()) {
			minerals.add(new MineralField(main, mineral));
		}
	}

	@Override
	public void onFrame() {
		outer: for (Unit myUnit : self.getUnits()) {
			for (MineralField field : minerals) {
				if (field.workers.contains(myUnit)) {
					continue outer;
				}
			}
			if (myUnit.getType().isWorker() && myUnit.isIdle() && myUnit.isCompleted()) {
				MineralField best = minerals.remove();
				best.workers.add(myUnit);
				minerals.add(best);
				myUnit.gather(best.field);
			}
		}
		for (MineralField field : minerals) {
			for (Unit worker : field.workers) {
				if (worker.getOrderTarget().getID() != field.field.getID() && worker.getOrderTarget().getType() == UnitType.Resource_Mineral_Field && !worker.isCarryingMinerals()) {
					if (field.field.isBeingGathered())
						worker.stop();
					else
						worker.gather(field.field);
				}
			}
		}
		
		// Debug information
//		for (Unit u : game.neutral().getUnits()) {
			//game.drawTextMap(u.getPosition(), "Distance: " + u.getDistance(main));
	//	}
		/*
		if (!game.getSelectedUnits().isEmpty()) {
			StringBuilder s = new StringBuilder("Selection:\n\n");
			for (Unit u : game.getSelectedUnits()) {
				s.append(u.getType())
				.append(" ").append((int) u.getDistance(main))
				.append(" ").append(u.getOrderTarget() == null ? "(none)" : u.getOrderTarget().getType())
				.append("\n");
				
				game.drawTextMap(u.getOrderTarget().getPosition(), "TARGET");
			}
			game.drawTextScreen(10,  10, s.toString());
		}
		*/
		for (MineralField field : minerals) {
			game.drawTextMap(field.field.getPosition(), ""+field.workers.size());
		}
	}
	
	@Override
	public void onEnd(boolean b) {
		System.out.println("Game over: " + b);
		System.exit(0);
	}
}

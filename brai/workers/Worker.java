package brai.workers;

import java.util.HashMap;
import java.util.Map;

import brai.Bot;
import brai.workers.carpentry.PlannedUnit;
import bwapi.Position;
import bwapi.Unit;

public class Worker {
	private static Map<Integer, Worker> cache = new HashMap<Integer, Worker>();

	public static Worker wrap(int id) {
		if (!cache.containsKey(id)) {
			cache.put(id, new Worker(id));
		}
		return cache.get(id);
	}

	public static Worker wrap(Unit u) {
		return wrap(u.getID());
	}

	public final int id;
	public final Unit unit;

	private Worker(int id) {
		this.id = id;
		this.unit = Bot.game.getUnit(id);
	}

	public static enum Job {
		IDLE, SCOUT, CARPENTER, REPAIRER, MINER, GASGATHERER
	}
	public Job job = Job.IDLE;
	public Unit target = null;
	public PlannedUnit plannedtarget = null;
	public Position scouttarget = null;
	public Integer idleTime = null;
	
	/*public boolean isScout() {
		return false;
	}

	public boolean isCarpenter() {
		return false;
	}

	public boolean isRepairer() {
		return false;
	}

	public boolean isMiner() {
		return false;
	}

	public boolean isGasGatherer() {
		return false;
	}

	public boolean needsJob() {
		return !isScout() && !isCarpenter() && !isRepairer() && !isMiner() && !isGasGatherer();
	}*/
}
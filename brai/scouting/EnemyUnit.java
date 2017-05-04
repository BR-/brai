package brai.scouting;

import java.util.HashMap;
import java.util.Map;

import brai.Bot;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class EnemyUnit {
	public int id;
	public Unit unit;
	public int firstSeenTime;
	public int lastSeenTime;
	public Position firstSeenPos;
	public Position lastSeenPos;
	public UnitType type;
	public boolean alive;
	
	private EnemyUnit(int id) {
		this.unit = Bot.game.getUnit(id);
		this.id = id;
		this.firstSeenTime = this.lastSeenTime = Bot.game.getFrameCount();
		this.firstSeenPos = this.lastSeenPos = unit.getPosition();
		this.type = unit.getType();
		this.alive = true;
	}
	
	public boolean isAlive() {
		return alive; // so we can stream.filter(EnemyUnit::isAlive)
	}
	
	private static Map<Integer, EnemyUnit> cache = new HashMap<Integer, EnemyUnit>();

	public static EnemyUnit wrap(int id) {
		if (!cache.containsKey(id)) {
			cache.put(id, new EnemyUnit(id));
		}
		return cache.get(id);
	}

	public static EnemyUnit wrap(Unit u) {
		return wrap(u.getID());
	}
}
package brai.workers.carpentry;

import brai.Bot;
import bwapi.TilePosition;
import bwapi.UnitType;

public class PlannedUnit {
	public UnitType type;
	public TilePosition position;
	public int time;
	public Progress progress;

	public PlannedUnit(UnitType type, TilePosition position) {
		this.type = type;
		this.position = position;
		this.time = Bot.game.getFrameCount();
		this.progress = Progress.PLANNED;
	}

	public TilePosition getPosition() {
		return position;
	}

	public enum Progress {
		PLANNED, // Added to construction queue
		ASSIGNED, // SCV has been designated
		CONSTRUCTING, // The building is in progress (but the SCV may have died)
		COMPLETE, // The building is done (and can be removed from the
					// construction system)
		BLOCKED; // The building cannot be made (due to lack of resources or
					// collision)
	}
}
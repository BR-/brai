package brai;

import java.util.Comparator;

import bwapi.Position;
import bwapi.Unit;

public class Util {
	public static Comparator<Position> furtherPos(Position base) {
		return (a, b) -> (int) (base.getDistance(a) - base.getDistance(b));
	}

	public static Comparator<Unit> furtherUnit(Position base) {
		return (a, b) -> (int) (base.getDistance(a) - base.getDistance(b));
	}
}
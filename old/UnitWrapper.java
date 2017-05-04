package old;

import java.util.HashMap;
import java.util.Map;

import bwapi.Unit;
import scai.Main;

public class UnitWrapper {
	public int id;
	public Unit unit;
	
	private static Map<Integer, UnitWrapper> wrappers = new HashMap<Integer, UnitWrapper>();
	
	public static UnitWrapper wrap(Unit unit) {
		return wrap(unit.getID());
	}
	
	public static UnitWrapper wrap(int id) {
		if (!wrappers.containsKey(id)) {
			wrappers.put(id, new UnitWrapper(id));
		}
		return wrappers.get(id);
	}
	
	private UnitWrapper(int id) {
		this.id = id;
		this.unit = Main.mirror.getGame().getUnit(id);
	}
}
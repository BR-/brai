package old;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bwapi.Unit;
import bwta.BaseLocation;

public class MineralField {
	public BaseLocation base;
	public Unit field;
	public double distance;
	public List<Unit> workers;
	
	public MineralField(BaseLocation base, Unit field) {
		this.base = base;
		this.field = field;
		this.distance = field.getDistance(base);
		this.workers = new ArrayList<Unit>();
	}
	
	public static class MFComparator implements Comparator<MineralField> {
		@Override
		public int compare(MineralField x, MineralField y) {
			if (x.workers.size() < 2 && y.workers.size() >= 2) {
				return -1;
			}
			if (x.workers.size() >= 2 && y.workers.size() < 2) {
				return 1;
			}
			return (int) (x.distance - y.distance); // loses precision but who cares about subpixels
		}
	}
}

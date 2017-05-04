package old;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import bwapi.Unit;
import scai.Bot;
import scai.workers.Worker.Job;

public class RepairController {
	public static class RepairOrder implements Comparable<RepairOrder> {
		public static enum Type {
			STOP_BURN, TO_FULL
		}
		public Unit target;
		public int priority;
		public Type type;
		public RepairOrder(Unit target, int priority, Type type) {
			this.target = target;
			this.priority = priority;
			this.type = type;
		}
		@Override
		public int compareTo(RepairOrder other) {
			return this.priority - other.priority;
		}
	}
	public static Queue<RepairOrder> orders = new PriorityQueue<RepairOrder>();
	
	public static void update() {
		Iterator<RepairOrder> it = orders.iterator();
		while (it.hasNext()) {
			RepairOrder order = it.next();
			if (!order.target.exists())
				it.remove();
		}
		Bot.getWorkers().filter(w -> w.job == Job.REPAIRER).forEach(w -> {
			if (!w.unit.isRepairing())
				w.job = Job.IDLE;
			if (!w.unit.getTarget().exists())
				w.job = Job.IDLE;
			if (w.unit.getTarget().getHitPoints() == w.unit.getTarget().getType().maxHitPoints())
				w.job = Job.IDLE;
		});
	}
	
	public static void repair(Unit target) {
		// request worker from WorkerController
		// create a RepairOrder for it
	}
}
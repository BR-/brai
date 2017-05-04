package brai.workers.carpentry;

import brai.Bot;
import brai.workers.Worker.Job;

public class RepairController {
	private static final int REPAIR_START = 1;
	private static final int REPAIR_STOP = 1;

	public static void update() {
		Bot.getWorkers().filter(w -> w.job == Job.REPAIRER).filter(w -> !w.target.exists()
				|| w.target.getHitPoints() >= w.target.getType().maxHitPoints() / REPAIR_STOP)
				.forEach(w -> {
					w.job = Job.IDLE;
					System.out
							.println("[Repair] Repair complete: " + w.target.getType().toString());
				});
		Bot.self.getUnits().stream()
				.filter(u -> u.exists() && u.isCompleted() && u.getType().isMechanical())
				.filter(u -> u.getHitPoints() < u.getType().maxHitPoints() / REPAIR_START)
				.forEach(u -> {
					if (Bot.getWorkers().noneMatch(w -> w.job == Job.REPAIRER && w.target == u)) {
						Bot.getWorkers().filter(w -> w.job == Job.IDLE || w.job == Job.MINER)
								.sorted((a, b) -> a.unit.getDistance(u.getPosition())
										- b.unit.getDistance(u.getPosition()))
								.findFirst().ifPresent(best -> {
									best.unit.repair(u);
									best.job = Job.REPAIRER;
									best.target = u;
									System.out.println("[Repair] Ordering repair for: "
											+ u.getType().toString());
								});
					}
				});
	}
}
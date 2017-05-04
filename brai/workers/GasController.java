package brai.workers;

import brai.Bot;
import brai.workers.Worker.Job;

public class GasController {
	private static final int NUM_GAS_GATHERERS = 0;

	public static void update() {
		Bot.self.getUnits().stream()
				.filter(u -> u.getType().isRefinery() && u.isCompleted() && u.exists())
				.forEach(refinery -> {
					long gatherers = Bot.getWorkers()
							.filter(w -> w.job == Job.GASGATHERER && w.target == refinery).count();
					while (gatherers < NUM_GAS_GATHERERS) {
						Bot.getWorkers().filter(w -> w.job == Job.MINER || w.job == Job.IDLE)
								.sorted((a, b) -> a.unit.getDistance(refinery.getPosition())
										- b.unit.getDistance(refinery.getPosition()))
								.findFirst().ifPresent(best -> {
									best.job = Job.GASGATHERER;
									best.target = refinery;
									// best.unit.returnCargo();
									// best.unit.gather(refinery, true);
									best.unit.gather(refinery);
								});
						++gatherers;
					}
				});

		Bot.getWorkers().filter(w -> w.job == Job.GASGATHERER).forEach(w -> {
			if (!w.target.exists()) {
				w.job = Job.IDLE;
			}
		});
	}
}
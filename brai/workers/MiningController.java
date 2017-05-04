package brai.workers;

import brai.Bot;
import brai.workers.Worker.Job;

public class MiningController {
	public static void update() {
		Bot.getWorkers().filter(w -> w.job == Job.IDLE).forEach(w -> {
			Bot.game.neutral().getUnits().stream().filter(u -> u.getInitialType().isMineralField())
					.min((a, b) -> w.unit.getDistance(a.getInitialPosition())
							- w.unit.getDistance(b.getInitialPosition()))
					.ifPresent(best -> {
						w.job = Job.MINER;
						w.target = best;
						w.unit.gather(best);
					});
		});
		Bot.getWorkers().filter(w -> w.job == Job.MINER).forEach(w -> {
			if (w.unit.isIdle() && !w.unit.isMoving() && !w.unit.isGatheringMinerals()) {
				if (w.unit.isCarryingMinerals()) {
					w.unit.returnCargo();
				} else {
					Bot.game.neutral().getUnits().stream()
							.filter(u -> u.getInitialType().isMineralField())
							.min((a, b) -> w.unit.getDistance(a.getInitialPosition())
									- w.unit.getDistance(b.getInitialPosition()))
							.ifPresent(best -> {
								w.target = best;
								w.unit.gather(best);
							});
				}
			}
		});
	}
}
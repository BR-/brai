package brai.workers.carpentry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import brai.Bot;
import brai.workers.Worker.Job;
import brai.workers.carpentry.PlannedUnit.Progress;
import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class CarpentryController {
	private static final int MINERAL_BLOCK_RANGE = 150;
	public static List<PlannedUnit> toBuild = new ArrayList<PlannedUnit>();

	public static void update() {
		String debug = "Carpentry queue:";
		for (PlannedUnit build : toBuild) {
			debug += "\n  " + build.type.toString() + ": " + build.progress;
		}
		Bot.game.drawTextScreen(10, 100, debug);
		Stream.concat(
				Bot.game.getNeutralUnits().stream()
						.filter(u -> u.getType().isMineralField()
								|| u.getType() == UnitType.Resource_Vespene_Geyser),
				Bot.self.getUnits().stream().filter(u -> u.getType().isRefinery()))
				.forEach(u -> Bot.game.drawCircleMap(u.getInitialPosition(), MINERAL_BLOCK_RANGE,
						Color.Black));
		for (PlannedUnit build : toBuild) {
			Position buildpos = build.position.toPosition();
			Position center = new Position(
					buildpos.getX()
							+ (build.type.dimensionLeft() + build.type.dimensionRight()) / 2,
					buildpos.getY() + (build.type.dimensionUp() + build.type.dimensionDown()) / 2);
			Bot.game.drawCircleMap(center, 100, Color.Brown);
			Bot.game.drawTextMap(build.position.toPosition(),
					build.type.toString() + "\n" + build.progress.toString());
			if (!Bot.getWorkers()
					.anyMatch(w -> w.job == Job.CARPENTER && w.plannedtarget == build)) {
				Bot.getWorkers().filter(w -> w.job == Job.IDLE || w.job == Job.MINER).sorted((a,
						b) -> (int) (a.unit.getTilePosition().getDistance(build.getPosition())
								- b.unit.getTilePosition().getDistance(build.getPosition())))
						.findFirst().ifPresent(best -> {
							build.time = Bot.game.getFrameCount();
							best.job = Job.CARPENTER;
							best.plannedtarget = build;
							// best.unit.returnCargo();
							// best.unit.move(build.position.toPosition(),
							// true);
							best.unit.move(build.position.toPosition());
							build.progress = Progress.ASSIGNED;
							System.out.println("[Construction] " + build.type + " planned");
						});
			}
		}
		// Bot.getWorkers().filter(w -> w.job == Job.CARPENTER)
		// .filter(w -> w.plannedtarget.progress == Progress.COMPLETE).forEach(w
		// -> {
		// w.job = Job.IDLE;
		// System.out.println("Construction is complete");
		// });
		// toBuild.removeIf(u -> u.progress == Progress.COMPLETE);
		Bot.getWorkers().filter(w -> w.job == Job.CARPENTER)
				.filter(w -> !w.unit.isConstructing() && !w.unit.isMoving()).forEach(w -> {
					if (w.unit.canBuild(w.plannedtarget.type, w.plannedtarget.position)) {
						w.unit.build(w.plannedtarget.type, w.plannedtarget.position);
						System.out.println("[Construction] " + w.plannedtarget.type + " started");
						w.plannedtarget.progress = Progress.CONSTRUCTING;
					} else if (Bot.game.getFrameCount() - w.plannedtarget.time > 1000) {
						w.job = Job.IDLE;
						if (Bot.self.getUnits().stream().anyMatch(u -> u.exists() && u.isCompleted()
								&& u.getType() == w.plannedtarget.type
								&& u.getTilePosition().getX() == w.plannedtarget.position.getX()
								&& u.getTilePosition().getY() == w.plannedtarget.position.getY())) {
							toBuild.removeIf(u -> u == w.plannedtarget);
							System.out
									.println("[Construction] " + w.plannedtarget.type + " is done");
							w.plannedtarget.progress = Progress.COMPLETE;

							// XXX THIS CODE SHOULD BE IN COMBATCONTROLLER
							if (w.plannedtarget.type == UnitType.Terran_Bunker) {
								Bot.self.getUnits().stream()
										.filter(u -> u.getType() == UnitType.Terran_Bunker)
										.forEach(Unit::unloadAll);
							}

						} else {
							System.out.println("[Construction] " + w.plannedtarget.type
									+ " blocked! Will retry");
							w.plannedtarget.progress = Progress.BLOCKED;
						}
					}
				});
	}

	public static boolean intersect(PlannedUnit pu, TilePosition pos) {
		return intersect(pu, pos.getX(), pos.getY());
	}

	public static boolean intersect(PlannedUnit pu, int x, int y) {
		return x >= pu.position.getX() - 2 && x <= pu.position.getY() + pu.type.tileWidth() + 2
				&& y >= pu.position.getY() - 2
				&& y <= pu.position.getY() + pu.type.tileHeight() + 2;
	}

	public static Predicate<PlannedUnit> overlap(PlannedUnit pu1) {
		return pu2 -> (

		pu1.position.getX() >= pu2.position.getX() - 1
				&& pu1.position.getX() <= pu2.position.getX() + pu2.type.tileWidth() + 1
				&& pu1.position.getY() >= pu2.position.getY() - 1
				&& pu1.position.getY() <= pu2.position.getY() + pu2.type.tileHeight() + 1)
				|| (pu1.position.getX() + pu1.type.tileWidth() >= pu2.position.getX() - 1
						&& pu1.position.getX() + pu1.type.tileWidth() <= pu2.position.getX()
								+ pu2.type.tileWidth() + 1
						&& pu1.position.getY() >= pu2.position.getY() - 1
						&& pu1.position.getY() <= pu2.position.getY() + pu2.type.tileHeight() + 1)
				|| (pu1.position.getX() + pu1.type.tileWidth() >= pu2.position.getX() - 1
						&& pu1.position.getX() + pu1.type.tileWidth() <= pu2.position.getX()
								+ pu2.type.tileWidth() + 1
						&& pu1.position.getY() + pu1.type.tileHeight() >= pu2.position.getY() - 1
						&& pu1.position.getY() + pu1.type.tileHeight() <= pu2.position.getY()
								+ pu2.type.tileHeight() + 1)
				|| (pu1.position.getX() >= pu2.position.getX() - 1
						&& pu1.position.getX() <= pu2.position.getX() + pu2.type.tileWidth() + 1
						&& pu1.position.getY() + pu1.type.tileHeight() >= pu2.position.getY() - 1
						&& pu1.position.getY() + pu1.type.tileHeight() <= pu2.position.getY()
								+ pu2.type.tileHeight() + 1);
	}

	public static boolean mineralBlocked(TilePosition tp) {
		return Stream
				.concat(Bot.game.getNeutralUnits().stream()
						.filter(u -> u.getType().isMineralField()
								|| u.getType() == UnitType.Resource_Vespene_Geyser),
						Bot.self.getUnits().stream().filter(u -> u.getType().isRefinery()))
				.anyMatch(u -> u.getInitialPosition()
						.getDistance(tp.toPosition()) < MINERAL_BLOCK_RANGE);
	}

	public static boolean mineralBlocked(PlannedUnit pu) {
		return mineralBlocked(pu.position)
				|| mineralBlocked(new TilePosition(pu.position.getX(),
						pu.position.getY() + pu.type.tileHeight()))
				|| mineralBlocked(new TilePosition(pu.position.getX() + pu.type.tileWidth(),
						pu.position.getY()))
				|| mineralBlocked(new TilePosition(pu.position.getX() + pu.type.tileWidth(),
						pu.position.getY() + pu.type.tileHeight()));
	}

	public static TilePosition getBuildPosition(UnitType type, TilePosition near, int maxDistance,
			Predicate<TilePosition> isValid) {
		TilePosition best = null;
		int bestd = maxDistance * maxDistance * 2;
		for (int dx = -maxDistance; dx < maxDistance; ++dx) {
			for (int dy = -maxDistance; dy < maxDistance; ++dy) {
				int dist = dx * dx + dy * dy;
				if (bestd > dist) {
					TilePosition pending = new TilePosition(near.getX() + dx, near.getY() + dy).makeValid();
					PlannedUnit pu = new PlannedUnit(type, pending);
					if (!mineralBlocked(pu)
							&& CarpentryController.toBuild.stream()
									.noneMatch(CarpentryController.overlap(pu))
							&& Bot.game.canBuildHere(pending, type) && isValid.test(pending)) {
						bestd = dist;
						best = pending;
					}
				}
			}
		}
		return best;
	}

	public static TilePosition getBuildPosition(UnitType type, TilePosition near, int maxDistance) {
		return getBuildPosition(type, near, maxDistance, dontcare -> true);
	}

	public static void build(UnitType type, TilePosition near) {
		TilePosition tp = null;
		int maxDistance = 10;
		while (tp == null) {
			tp = getBuildPosition(type, near, maxDistance);
			maxDistance *= 2;
		}
		toBuild.add(new PlannedUnit(type, tp));
	}

	public static boolean build(UnitType type, TilePosition near, int maxDistance) {
		return build(type, near, maxDistance, dontcare -> true);
	}

	public static boolean build(UnitType type, TilePosition near, int maxDistance,
			Predicate<TilePosition> isValid) {
		TilePosition tp = getBuildPosition(type, near, maxDistance, isValid);
		if (tp != null) {
			toBuild.add(new PlannedUnit(type, tp));
			return true;
		} else {
			return false;
		}
	}
}
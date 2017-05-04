package brai.scouting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import brai.Bot;
import brai.workers.Worker.Job;
import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

public class ScoutController {
	private static final int SCOUT_DISTANCE = 300;
	private static Set<EnemyUnit> scoutedUnits = new HashSet<EnemyUnit>();
	private static List<Position> toScout = new ArrayList<Position>();

	static {
		BWTA.getStartLocations().stream()
				.filter(l -> l.getPosition().getApproxDistance(Bot.main.getPosition()) > 100)
				.forEach(l -> toScout.add(l.getPosition()));
		BWTA.getBaseLocations().stream()
				.filter(l -> l.getPosition().getApproxDistance(Bot.main.getPosition()) > 100)
				.forEach(l -> toScout.add(l.getPosition()));
		System.out.println("[Scout] Potential base locations: " + toScout.size());
	}

	public static void update() {
		scoutedUnits.stream().filter(EnemyUnit::isAlive).forEach(e -> {
			Bot.game.drawCircleMap(e.lastSeenPos, 25, Color.Red);
			Bot.game.drawTextMap(e.lastSeenPos, e.type.toString());
		});
		if (toScout.isEmpty()) {
			if (scoutedUnits.isEmpty()) {
				BWTA.getStartLocations().stream().filter(
						l -> l.getPosition().getApproxDistance(Bot.main.getPosition()) > 100)
						.forEach(l -> toScout.add(l.getPosition()));
				System.out.println("[Scout] Re-adding start locations");
			} else {
				int x = 0;
				int y = 0;
				int c = 0;
				for (EnemyUnit e : scoutedUnits) {
					if (e.alive && !e.type.canMove()) {
						x += e.firstSeenPos.getX();
						y += e.firstSeenPos.getY();
						++c;
					}
				}
				toScout.add(new Position(x / c, y / c));
				System.out.println("[Scout] Adding centroid to list");
			}
		}
		if (!toScout.isEmpty() && Bot.getWorkers().noneMatch(w -> w.job == Job.SCOUT)
				&& (scoutedUnits.isEmpty() && Bot.getWorkers().count() >= 11
						|| Bot.getWorkers().count() >= 20)) {
			Bot.getWorkers().filter(w -> w.job == Job.MINER || w.job == Job.IDLE).findAny()
					.ifPresent(scout -> {
						scout.job = Job.SCOUT;
						System.out.println("[Scout] Assigned a scout"); //TODO take farthest?
					});
		}
		Bot.getWorkers().filter(w -> w.job == Job.SCOUT).forEach(w -> {
			if (w.unit.isIdle() || !w.unit.isMoving()) {
				if (w.idleTime == null) {
					w.idleTime = Bot.game.getFrameCount();
				} else if (Bot.game.getFrameCount() - w.idleTime > 1000) {
					w.scouttarget = null;
					w.job = Job.IDLE;
					return;
				}
			} else {
				w.idleTime = null;
			}
			if (!toScout.isEmpty() && w.scouttarget == null) {
				w.scouttarget = toScout.remove(0);
				System.out.println("[Scout] Set initial scouting location");
				w.unit.move(w.scouttarget);
			}
			while (!toScout.isEmpty() && w.unit.getDistance(w.scouttarget) < SCOUT_DISTANCE) {
				w.scouttarget = toScout.remove(0);
				System.out.println("[Scout] Updated scouting location");
				w.unit.move(w.scouttarget);
			}
			if (toScout.isEmpty() && (w.scouttarget == null
					|| w.unit.getDistance(w.scouttarget) < SCOUT_DISTANCE)) {
				w.scouttarget = null;
				w.job = Job.IDLE;
			}
		});
		Bot.enemy.getUnits().stream().map(EnemyUnit::wrap).forEach(e -> {
			if (scoutedUnits.contains(e)) {
				if (e.unit.exists()) {
					e.lastSeenPos = e.unit.getPosition();
					e.lastSeenTime = Bot.game.getFrameCount();
				}
			} else {
				System.out.println("[Scout] Found new enemy unit: " + e.type.toString());
				scoutedUnits.add(e);
				toScout.add(e.lastSeenPos);
			}
		});
	}

	public static void onUnitDestroy(Unit u) {
		if (u.getPlayer().getID() != Bot.self.getID()) {
			EnemyUnit.wrap(u).alive = false;
		}
	}

	public static Position getEnemyBase() {
		return scoutedUnits.stream().filter(u -> u.type.isResourceDepot() && u.alive).findFirst()
				.map(u -> u.lastSeenPos).orElseGet(() -> {
					if (scoutedUnits.stream().noneMatch(u -> u.alive)) {
						for (BaseLocation start : BWTA.getStartLocations()) {
							if (start.getAirDistance(BWTA.getStartLocation(Bot.self)) > 100) {
								return start.getPosition();
							}
						}
						System.err.println(
								"[Scout] CANNOT FIND ENEMY BASE???? THIS SHOULD NEVER HAPPEN");
						Bot.game.pauseGame();
						System.exit(1);
						return null;
					} else {
						return scoutedUnits.stream().filter(u -> u.alive).findAny()
								.get().lastSeenPos;
					}
				});
	}
}
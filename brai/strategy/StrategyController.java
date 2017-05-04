package brai.strategy;

import java.util.List;

import brai.Bot;
import brai.combat.CombatController;
import brai.workers.carpentry.CarpentryController;
import brai.workers.carpentry.PlannedUnit;
import bwapi.Color;
import bwapi.Pair;
import bwapi.Position;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import bwta.Region;

public class StrategyController {
	public static BaseLocation natural = BWTA.getBaseLocations().stream()
			.filter(b -> !b.isStartLocation())
			.sorted((a, b) -> (int) (a.getDistance(Bot.main) - b.getDistance(Bot.main))).findFirst()
			.get();

	private static boolean depotPlanned = false;
	private static boolean ccPlanned = false;
	private static boolean gasPlanned = false;
	private static int scvPlanned = 0;
	private static boolean barracksPlanned = false;
	private static boolean depot2Planned = false;
	public static int rinePlanned = 0;

	public static void update() {
		List<Position> perimeter;
		for (Region r : BWTA.getRegions()) {
			perimeter = r.getPolygon().getPoints();
			for (int i = 0; i < perimeter.size(); ++i) {
				Bot.game.drawLineMap(perimeter.get(i), perimeter.get((i + 1) % perimeter.size()),
						Color.Grey);
			}
		}
		Pair<Region, Region> reg = CombatController.mainChoke.getRegions();
		Region mainRegion; // BWTA.getRegion(Bot.main.getPosition()) might fail
							// in case the choke isn't directly adjacent
							// (is this possible?)
		if (reg.first.getPolygon().isInside(Bot.main.getPosition())) {
			mainRegion = reg.first;
		} else {
			mainRegion = reg.second;
		}
		perimeter = mainRegion.getPolygon().getPoints();
		for (int i = 0; i < perimeter.size(); ++i) {
			Bot.game.drawLineMap(perimeter.get(i), perimeter.get((i + 1) % perimeter.size()),
					Color.Yellow);
		}
		reg = CombatController.natChoke.getRegions();
		Region natRegion;
		if (reg.first.getPolygon().isInside(natural.getPosition())) {
			natRegion = reg.first;
		} else {
			natRegion = reg.second;
		}
		perimeter = natRegion.getPolygon().getPoints();
		for (int i = 0; i < perimeter.size(); ++i) {
			Bot.game.drawLineMap(perimeter.get(i), perimeter.get((i + 1) % perimeter.size()),
					Color.Orange);
		}
		Bot.game.drawCircleMap(CombatController.mainChoke.getCenter(), 320, Color.Purple);
		Bot.game.drawCircleMap(CombatController.natChoke.getCenter(), 320, Color.Purple);
		Bot.game.drawCircleMap(natural.getPosition(), 25, Color.Red);
		Bot.game.drawCircleMap(natural.getPosition(), 50, Color.Red);
		Bot.game.drawCircleMap(natural.getPosition(), 100, Color.Red);

		long availableMinerals = Bot.self.minerals()
				- CarpentryController.toBuild.stream().mapToLong(u -> u.type.mineralPrice()).sum();

		if (Bot.self.supplyUsed() >= 9 * 2 && !depotPlanned) {
			if (availableMinerals >= UnitType.Terran_Supply_Depot.mineralPrice()) {
				depotPlanned = true;
				System.out.println("[Strategy] Planning depot");
				/*
				 * TilePosition tp = Bot.main.getTilePosition(); while
				 * (!Bot.game.canBuildHere(tp, UnitType.Terran_Supply_Depot) ||
				 * CarpentryController.toBuild.stream().anyMatch(
				 * CarpentryController .overlap(new
				 * PlannedUnit(UnitType.Terran_Supply_Depot, tp)))) { tp = new
				 * TilePosition(tp.getX() + 1, tp.getY() + 1); }
				 * CarpentryController.toBuild.add(new
				 * PlannedUnit(UnitType.Terran_Supply_Depot, tp));
				 */
				CarpentryController.build(UnitType.Terran_Supply_Depot, Bot.main.getTilePosition());
			}
		} else if (Bot.self.supplyUsed() >= 14 * 2 && !ccPlanned) {
			if (availableMinerals >= UnitType.Terran_Command_Center.mineralPrice()) {
				ccPlanned = true;
				System.out.println("[Strategy] Planning expansion");
				// BaseLocation expansion =
				// BWTA.getNearestBaseLocation(Bot.main.getPosition());
				CarpentryController.toBuild.add(
						new PlannedUnit(UnitType.Terran_Command_Center, natural.getTilePosition()));
			}
		} else if (Bot.self.supplyUsed() >= 15 * 2 && !barracksPlanned) {
			if (availableMinerals >= UnitType.Terran_Barracks.mineralPrice()) {
				barracksPlanned = true;
				System.out.println("[Strategy] Planning barracks");
				/*
				 * TilePosition tp = Bot.main.getTilePosition(); while
				 * (!Bot.game.canBuildHere(tp, UnitType.Terran_Barracks) ||
				 * CarpentryController.toBuild.stream().anyMatch(
				 * CarpentryController .overlap(new
				 * PlannedUnit(UnitType.Terran_Barracks, tp)))) { tp = new
				 * TilePosition(tp.getX() - 1, tp.getY() - 1); }
				 * CarpentryController.toBuild.add(new
				 * PlannedUnit(UnitType.Terran_Barracks, tp));
				 */
				CarpentryController.build(UnitType.Terran_Barracks, Bot.main.getTilePosition());
			}
		} else if (Bot.self.supplyUsed() >= 16 * 2 && !gasPlanned) {
			if (availableMinerals >= UnitType.Terran_Refinery.mineralPrice()) {
				gasPlanned = true;
				System.out.println("[Strategy] Planning gas");
				CarpentryController.toBuild.add(new PlannedUnit(UnitType.Terran_Refinery,
						BWTA.getStartLocation(Bot.self).getGeysers().get(0).getTilePosition()));
			}
		} else if (Bot.self.supplyUsed() >= 16 * 2 && !depot2Planned) {
			if (availableMinerals >= UnitType.Terran_Supply_Depot.mineralPrice()) {
				depot2Planned = true;
				System.out.println("[Strategy] Planning second depot");
				/*
				 * TilePosition tp = Bot.main.getTilePosition(); while
				 * (!Bot.game.canBuildHere(tp, UnitType.Terran_Supply_Depot) ||
				 * CarpentryController.toBuild.stream().anyMatch(
				 * CarpentryController .overlap(new
				 * PlannedUnit(UnitType.Terran_Supply_Depot, tp)))) { tp = new
				 * TilePosition(tp.getX() + 1, tp.getY() + 1); }
				 * CarpentryController.toBuild.add(new
				 * PlannedUnit(UnitType.Terran_Supply_Depot, tp));
				 */
				CarpentryController.build(UnitType.Terran_Supply_Depot, Bot.main.getTilePosition());
			}
		} else if (Bot.self.supplyUsed() >= 20 * 2 && Bot.self.supplyTotal() - Bot.self.supplyUsed()
				+ CarpentryController.toBuild.stream()
						.filter(p -> p.type == UnitType.Terran_Supply_Depot).count()
						* UnitType.Terran_Supply_Depot.supplyProvided() <= 2
								* (3 + 2 * Bot.self.getUnits().stream()
										.filter(u -> u.getType() == UnitType.Terran_Barracks)
										.count())) {
			if (availableMinerals >= UnitType.Terran_Supply_Depot.mineralPrice()) {
				System.out.println("[Strategy] Planning extra depot");
				/*
				 * TilePosition tp = Bot.main.getTilePosition(); while
				 * (!Bot.game.canBuildHere(tp, UnitType.Terran_Supply_Depot) ||
				 * CarpentryController.toBuild.stream().anyMatch(
				 * CarpentryController .overlap(new
				 * PlannedUnit(UnitType.Terran_Supply_Depot, tp)))) { tp = new
				 * TilePosition(tp.getX(), tp.getY() + (tp.getY() <
				 * Bot.game.mapHeight() / 2 ? 1 : -1)); }
				 * CarpentryController.toBuild.add(new
				 * PlannedUnit(UnitType.Terran_Supply_Depot, tp));
				 */
				CarpentryController.build(UnitType.Terran_Supply_Depot, Bot.main.getTilePosition());
			}
		} else if (Bot.self.supplyUsed() >= 20 * 2 && (Bot.self.getUnits().stream()
				.filter(u -> u.getType() == UnitType.Terran_Bunker && u.exists() && u.isCompleted())
				.count()
				+ CarpentryController.toBuild.stream().filter(p -> p.type == UnitType.Terran_Bunker)
						.count()) < 6) {
			if (availableMinerals >= UnitType.Terran_Bunker.mineralPrice()) {
				Chokepoint choke;
				if ((Bot.self.getUnits().stream().filter(
						u -> u.getType() == UnitType.Terran_Bunker && u.exists() && u.isCompleted())
						.filter(u -> u.getPosition()
								.getApproxDistance(CombatController.natChoke.getPoint()) < 320)
						.count()
						+ CarpentryController.toBuild.stream()
								.filter(p -> p.type == UnitType.Terran_Bunker
										&& p.position.toPosition().getApproxDistance(
												CombatController.natChoke.getPoint()) < 320)
								.count()) < 3) {
					choke = CombatController.natChoke;
				} else {
					choke = CombatController.mainChoke;
				}
				if (!CarpentryController.build(UnitType.Terran_Bunker,
						choke.getPoint().toTilePosition(), 10,
						pos -> (choke == CombatController.mainChoke ? mainRegion : natRegion)
								.getPolygon().isInside(pos.toPosition()))) {
					System.err.println("[Strategy] CANNOT BUILD BUNKER!!!");
					return;
				}
				System.out.println("[Strategy] Planning bunker");
			}
		} else if (Bot.self.supplyUsed() >= 20 * 2 && (Bot.self.getUnits().stream()
				.filter(u -> u.exists() && u.getType() == UnitType.Terran_Marine).count()
				/ 20 > (Bot.self.getUnits().stream()
						.filter(u -> u.exists() && u.isCompleted()
								&& u.getType() == UnitType.Terran_Barracks)
						.count()
						+ CarpentryController.toBuild.stream()
								.filter(u -> u.type == UnitType.Terran_Barracks).count())
				|| availableMinerals >= 750)) {
			if (availableMinerals >= UnitType.Terran_Barracks.mineralPrice()) {
				System.out.println("[Strategy] Planning extra barracks");
				/*
				 * TilePosition tp = Bot.main.getTilePosition(); while
				 * (!Bot.game.canBuildHere(tp, UnitType.Terran_Barracks) ||
				 * CarpentryController.toBuild.stream().anyMatch(
				 * CarpentryController .overlap(new
				 * PlannedUnit(UnitType.Terran_Barracks, tp)))) { tp = new
				 * TilePosition( tp.getX() + (tp.getX() < Bot.game.mapWidth() /
				 * 2 ? 1 : -1), tp.getY()); }
				 * CarpentryController.toBuild.add(new
				 * PlannedUnit(UnitType.Terran_Barracks, tp));
				 */
				CarpentryController.build(UnitType.Terran_Barracks, Bot.main.getTilePosition());
			}
		} else if (Bot.self.supplyUsed() >= 22 * 2 && (Bot.self.getUnits().stream()
				.filter(u -> u.exists() && u.isCompleted()
						&& u.getType() == UnitType.Terran_Barracks)
				.count()
				+ CarpentryController.toBuild.stream()
						.filter(u -> u.type == UnitType.Terran_Barracks).count()) <= 3) {
			if (availableMinerals >= UnitType.Terran_Barracks.mineralPrice()) {
				System.out.println("[Strategy] Planning necessary barracks");
				/*
				 * TilePosition tp = Bot.main.getTilePosition(); while
				 * (!Bot.game.canBuildHere(tp, UnitType.Terran_Barracks) ||
				 * CarpentryController.toBuild.stream().anyMatch(
				 * CarpentryController .overlap(new
				 * PlannedUnit(UnitType.Terran_Barracks, tp)))) { tp = new
				 * TilePosition( tp.getX() + (tp.getX() < Bot.game.mapWidth() /
				 * 2 ? 1 : -1), tp.getY()); }
				 * CarpentryController.toBuild.add(new
				 * PlannedUnit(UnitType.Terran_Barracks, tp));
				 */
				CarpentryController.build(UnitType.Terran_Barracks, Bot.main.getTilePosition());
			}
		} else {
			if (Bot.getWorkers().count() < 25) {
				long scvAfford = Math.min(availableMinerals / UnitType.Terran_SCV.mineralPrice(),
						(Bot.self.supplyTotal() - Bot.self.supplyUsed())
								/ UnitType.Terran_SCV.supplyRequired());
				if (scvAfford > 0) {
					Bot.self.getUnits().stream()
							.filter(u -> u.getType().isResourceDepot() && u.isCompleted()
									&& u.exists())
							.limit(scvAfford).filter(u -> u.getTrainingQueue().isEmpty())
							.forEach(u -> {
								u.train(UnitType.Terran_SCV);
								System.out.println("[Strategy] Training SCV " + scvPlanned);
								++scvPlanned;
							});
				}
			}

			if (barracksPlanned) {
				long rineAfford = Math.min(
						availableMinerals / UnitType.Terran_Marine.mineralPrice(),
						(Bot.self.supplyTotal() - Bot.self.supplyUsed())
								/ UnitType.Terran_Marine.supplyRequired());
				if (rineAfford > 0) {
					Bot.self.getUnits().stream()
							.filter(u -> u.getType() == UnitType.Terran_Barracks && u.isCompleted()
									&& u.exists())
							.limit(rineAfford).filter(u -> u.getTrainingQueue().isEmpty())
							.forEach(u -> {
								u.train(UnitType.Terran_Marine);
								System.out.println("[Strategy] Training marine " + rinePlanned);
								++rinePlanned;
							});
				}
			}
		}
	}
}
package brai.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import brai.Bot;
import brai.scouting.ScoutController;
import brai.strategy.StrategyController;
import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;

public class CombatController {
	public static Chokepoint mainChoke = BWTA.getNearestChokepoint(Bot.main.getPosition());
	public static Chokepoint natChoke = BWTA.getChokepoints().stream()
			.filter(c -> c.getCenter().getApproxDistance(mainChoke.getCenter()) > 25)
			.sorted((a,
					b) -> (int) (a.getDistance(StrategyController.natural.getPosition())
							- b.getDistance(StrategyController.natural.getPosition())))
			.findFirst().get();
	public static List<Unit> onTheAttack = new ArrayList<Unit>();

	public static void update() {
		onTheAttack.removeIf(u -> !u.exists());
		Bot.game.getNukeDots().forEach(dot -> {
			// this seems to be quite inaccurate.. i wonder why..
			Bot.game.drawLineMap(dot.getX() - 400, dot.getY() - 400, dot.getX() + 400,
					dot.getY() + 400, Color.White);
			Bot.game.drawLineMap(dot.getX() + 400, dot.getY() - 400, dot.getX() - 400,
					dot.getY() + 400, Color.White);
			Bot.game.drawLineMap(dot.getX() - 400, dot.getY(), dot.getX() + 400, dot.getY(),
					Color.White);
			Bot.game.drawLineMap(dot.getX(), dot.getY() - 400, dot.getX(), dot.getY() + 400,
					Color.White);
			Bot.game.drawCircleMap(dot, 400, Color.White);
			Bot.game.drawLineMap(new Position(Bot.game.getScreenPosition().getX() + 640 / 2,
					Bot.game.getScreenPosition().getY() + 480 / 2), dot, Color.White);
			Bot.game.drawTextMap(dot, "HOLY SHIT A NUKE");
			Bot.game.drawTextScreen(250, 250, "HOLY SHIT NUKES ARE COMING");
		});
		{
			long bunked = Bot.self.getUnits().stream()
					.filter(u -> u.getType() == UnitType.Terran_Bunker)
					.mapToLong(u -> u.getLoadedUnits().size()).sum();
			long reserve = Bot.self.getUnits().stream()
					.filter(u -> u.getType() == UnitType.Terran_Marine && !u.isLoaded()
							&& u.exists() && u.isCompleted() && !onTheAttack.contains(u))
					.count();
			long army = onTheAttack.size();
			long inProgress = Bot.self.getUnits().stream()
					.filter(u -> u.getType() == UnitType.Terran_Marine && !u.isCompleted()).count();
			String debug = "Marines:";
			debug += "\n  Created: " + StrategyController.rinePlanned;
			debug += "\n  In a bunker: " + bunked;
			debug += "\n  In reserve: " + reserve;
			debug += "\n  In army: " + army;
			debug += "\n  Died: "
					+ (StrategyController.rinePlanned - bunked - reserve - army - inProgress);
			Bot.game.drawTextScreen(10, 10, debug);
		}
		BWTA.getChokepoints().stream()
				.forEach(c -> Bot.game.drawCircleMap(c.getCenter(), 30, Color.Orange));
		Bot.game.drawCircleMap(mainChoke.getCenter(), 40, Color.Green);
		Bot.game.drawCircleMap(natChoke.getCenter(), 50, Color.Teal);
		Bot.self.getUnits()
				.stream().filter(u -> u.getType() == UnitType.Terran_Marine && u.exists()
						&& u.isCompleted() && !u.isLoaded() && !onTheAttack.contains(u))
				.forEach(u -> {
					/*
					 * if (u.getHitPoints() < u.getType().maxHitPoints() / 2) {
					 * if (u.getPosition().getDistance(mainChoke.getCenter()) >
					 * 250) { u.attack(mainChoke.getCenter()); } else {
					 * u.holdPosition(); } } else if (!u.isMoving()) { if
					 * (u.getPosition().getDistance(natChoke.getCenter()) > 250)
					 * { u.attack(natChoke.getCenter()); } else {
					 * u.holdPosition(); } }
					 */
					if (u.getTarget() != null
							&& u.getTarget().getType() == UnitType.Terran_Bunker) {
						if (u.getTarget().getLoadedUnits().size() == 4) {
							u.holdPosition();
						}
					} else {
						if (!u.isMoving()) {
							if (u.getPosition().getDistance(natChoke.getCenter()) > 250) {
								u.attack(natChoke.getCenter());
							} else {
								u.patrol(mainChoke.getCenter());
							}
						}
					}
				});
		Bot.self.getUnits().stream()
				.filter(u -> u.getType() == UnitType.Terran_Bunker && u.exists() && u.isCompleted()
						&& u.getLoadedUnits().size() < u.getType().spaceProvided())
				.sorted((a,
						b) -> (int) -(a.getDistance(Bot.main.getPosition())
								- b.getDistance(Bot.main.getPosition()))) // farthest
																			// takes
																			// priority
				.forEach(bunker -> {
					Bot.self.getUnits().stream()
							.filter(u -> u.getType() == UnitType.Terran_Marine && u.exists()
									&& u.isCompleted() && !u.isLoaded()
									&& !(u.getTarget() != null
											&& u.getTarget().getType() == UnitType.Terran_Bunker)
									&& !onTheAttack.contains(u))
							.sorted((a,
									b) -> (int) (a.getDistance(bunker.getPosition())
											- b.getDistance(bunker.getPosition())))
							.limit(bunker.getType().spaceProvided()
									- bunker.getLoadedUnits().size())
							.forEach(u -> {
								// u.move(bunker.getPosition());
								u.load(bunker);
							});
				});
		Bot.self.getUnits().stream()
				.filter(u -> u.getType() == UnitType.Terran_Bunker && u.exists() && u.isCompleted())
				.forEach(bunker -> {
					Bot.game.setTextSize(bwapi.Text.Size.Enum.Large);
					Bot.game.drawTextMap(bunker.getPosition(),
							"[" + bunker.getLoadedUnits().size() + "]");
					Bot.game.setTextSize();
					Bot.enemy.getUnits().stream()
							.sorted((a,
									b) -> (int) (a.getDistance(bunker.getPosition())
											- b.getDistance(bunker.getPosition())))
							.findFirst().ifPresent(u -> bunker.attack(u));
				});
		Supplier<Stream<Unit>> army = () -> Bot.self.getUnits().stream()
				.filter(u -> u.getType() == UnitType.Terran_Marine && u.exists() && u.isCompleted()
						&& !u.isLoaded()
						&& !(u.getTarget() != null
								&& u.getTarget().getType() == UnitType.Terran_Bunker)
						&& !onTheAttack.contains(u));
		Position atk = ScoutController.getEnemyBase();
		if (army.get().count() >= 100) {
			army.get()
					.sorted((a,
							b) -> (int) (a.getDistance(Bot.main.getPosition())
									- b.getDistance(Bot.main.getPosition())))
					.limit(75).peek(onTheAttack::add).forEach(u -> u.attack(atk));
		}
		onTheAttack.forEach(u -> {
			if (/* u.isMoving() || */ u.isAttackFrame() || u.isAttacking()
					|| u.isStartingAttack()) {
				Bot.game.drawTextMap(u.getPosition(), "atk");
			} else {
				u.attack(atk);
				Bot.game.drawTextMap(u.getPosition(), "mov");
			}
		});
	}
}
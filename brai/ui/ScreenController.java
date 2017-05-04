package brai.ui;

import java.util.stream.Collectors;

import brai.Bot;
import brai.Util;
import brai.combat.CombatController;
import brai.workers.Worker.Job;
import brai.workers.carpentry.CarpentryController;
import brai.workers.carpentry.PlannedUnit.Progress;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class ScreenController {
	public static void update() {
		Position screen = null;
		screen = Bot.getWorkers().findAny().map(u -> u.unit.getPosition()).orElse(screen);
		screen = Bot.self.getUnits().stream().filter(u -> u.getType() == UnitType.Terran_Marine)
				.collect(Collectors.maxBy(Util.furtherUnit(Bot.main.getPosition())))
				.map(Unit::getPosition).orElse(screen);
		screen = Bot.getWorkers().filter(u -> u.job == Job.REPAIRER).findAny()
				.map(u -> u.unit.getPosition()).orElse(screen);
		screen = Bot.getWorkers().filter(u -> u.job == Job.SCOUT).findAny()
				.map(u -> u.unit.getPosition()).orElse(screen);
		screen = CarpentryController.toBuild.stream()
				.filter(b -> b.progress != Progress.CONSTRUCTING).findAny()
				.map(u -> u.position.toPosition()).orElse(screen);
		screen = CombatController.onTheAttack.stream()
				.collect(Collectors.maxBy(Util.furtherUnit(Bot.main.getPosition())))
				.map(Unit::getPosition).orElse(screen);
		screen = Bot.game.getNukeDots().stream().findAny().orElse(screen);
		if (screen != null) {
			Bot.game.setScreenPosition(screen.getX() - 640 / 2, screen.getY() - 480 / 2);
		}
	}
}
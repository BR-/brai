package brai.workers;

import brai.Bot;

public class WorkerController {
	public static void update() {
		if (!Bot.game.isPaused() && Bot.getWorkers().count() == 0) {
			Bot.game.sendText("gg");
			Bot.game.pauseGame();
		}
	}
}
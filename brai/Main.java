package brai;

import java.io.IOException;

import bwapi.Mirror;

public class Main {
	public static Mirror mirror;

	public static void main(String[] args) {
		if (true) {
			try {
				Runtime.getRuntime().exec("taskkill /f /im starcraft.exe");
				Runtime.getRuntime().exec("taskkill /f /im chaoslauncher.exe");
				Thread.sleep(250);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

			new Thread() {
				public void run() {
					try {
						Thread.sleep(250);
						Runtime.getRuntime()
								.exec("D:\\StarCraft\\BWAPI\\Chaoslauncher\\Chaoslauncher.exe");
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}

		mirror = new Mirror();
		mirror.getModule().setEventListener(new BetterBWListener(new Bot()));
		mirror.startGame();
	}
}
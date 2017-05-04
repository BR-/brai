package brai;

import bwapi.BWEventListener;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;

public class BetterBWListener implements BWEventListener {
	private BWEventListener proxy;

	public BetterBWListener(BWEventListener proxy) {
		this.proxy = proxy;
	}

	@Override
	public void onStart() {
		try {
			proxy.onStart();
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onEnd(boolean isWinner) {
		try {
			proxy.onEnd(isWinner);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onFrame() {
		try {
			proxy.onFrame();
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onSendText(String text) {
		try {
			proxy.onSendText(text);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onReceiveText(Player player, String text) {
		try {
			proxy.onReceiveText(player, text);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}

	}

	@Override
	public void onPlayerLeft(Player player) {
		try {
			proxy.onPlayerLeft(player);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onNukeDetect(Position target) {
		try {
			proxy.onNukeDetect(target);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitDiscover(Unit unit) {
		try {
			proxy.onUnitDiscover(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitEvade(Unit unit) {
		try {
			proxy.onUnitEvade(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitShow(Unit unit) {
		try {
			proxy.onUnitShow(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitHide(Unit unit) {
		try {
			proxy.onUnitHide(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {
		try {
			proxy.onUnitCreate(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		try {
			proxy.onUnitDestroy(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitMorph(Unit unit) {
		try {
			proxy.onUnitMorph(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitRenegade(Unit unit) {
		try {
			proxy.onUnitRenegade(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onSaveGame(String gameName) {
		try {
			proxy.onSaveGame(gameName);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onUnitComplete(Unit unit) {
		try {
			proxy.onUnitComplete(unit);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void onPlayerDropped(Player player) {
		try {
			proxy.onPlayerDropped(player);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
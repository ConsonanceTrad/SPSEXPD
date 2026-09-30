/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.levels;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.mobs.Mob;
import pd.levels.features.LevelTransition;
import pd.scenes.GameScene;
import watabou.utils.Bundle;

/** Shared lifecycle for the legacy 48x48 sewer and prison boss maps. */
abstract class SpsFixedBossLevel extends Level {

	static final int WIDTH = 48;
	static final int HEIGHT = 48;
	static final int ENTRANCE = 23 + WIDTH * 15;
	static final int EXIT = 23 + WIDTH * 37;
	static final int BOSS_CELL = 23 + WIDTH * 21;

	private boolean enteredArena;

	protected abstract int[] legacyMap();
	protected abstract Mob createLegacyBoss();
	protected abstract Class<? extends Mob> legacyBossClass();
	protected boolean isLegacyBossActor(Mob mob) { return legacyBossClass().isInstance(mob); }
	protected void storeBossSelection(Bundle bundle) { }
	protected void restoreBossSelection(Bundle bundle) { }

	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		int[] source = legacyMap();
		if (source.length != length()) return false;
		map = source.clone();
		transitions.add(new LevelTransition(this, ENTRANCE,
				LevelTransition.Type.REGULAR_ENTRANCE,
				Dungeon.depth - 1, Dungeon.branch, LevelTransition.Type.REGULAR_EXIT));
		transitions.add(new LevelTransition(this, EXIT,
				LevelTransition.Type.REGULAR_EXIT,
				Dungeon.depth + 1, Dungeon.branch, LevelTransition.Type.REGULAR_ENTRANCE));
		locked = false;
		return true;
	}

	@Override protected void createMobs() { }
	@Override protected void createItems() { }
	@Override public Mob createMob() { return null; }
	@Override public Actor addRespawner() { return null; }
	@Override public int randomRespawnCell(Char ch) { return -1; }

	@Override
	public void pressCell(int cell) {
		super.pressCell(cell);
		if (!enteredArena && Dungeon.hero != null && Dungeon.hero.pos == cell && cell != ENTRANCE) {
			enteredArena = true;
			seal();
			spawnBoss();
			Dungeon.observe();
		}
	}

	private void spawnBoss() {
		Mob boss = createLegacyBoss();
		boss.pos = BOSS_CELL;
		boss.state = boss.HUNTING;
		GameScene.add(boss);
		boss.notice();
	}

	@Override
	public void seal() {
		if (!locked) {
			super.seal();
			set(ENTRANCE, Terrain.WALL_DECO);
			GameScene.updateMap(ENTRANCE);
			GameScene.ripple(ENTRANCE);
		}
	}

	@Override
	public void unseal() {
		if (locked) {
			super.unseal();
			set(ENTRANCE, Terrain.ENTRANCE);
			set(EXIT, Terrain.EXIT);
			GameScene.updateMap(ENTRANCE);
			GameScene.updateMap(EXIT);
		}
	}

	private static final String ENTERED = "entered";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ENTERED, enteredArena);
		storeBossSelection(bundle);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		restoreBossSelection(bundle);
		enteredArena = bundle.getBoolean(ENTERED);
		if (enteredArena && map[EXIT] == Terrain.LOCKED_EXIT) {
			locked = true;
			boolean found = false;
			for (Mob mob : mobs) {
				if (isLegacyBossActor(mob)) {
					found = true;
					break;
				}
			}
			if (!found) {
				Mob boss = createLegacyBoss();
				boss.pos = BOSS_CELL;
				boss.state = boss.HUNTING;
				mobs.add(boss);
			}
		}
	}
}

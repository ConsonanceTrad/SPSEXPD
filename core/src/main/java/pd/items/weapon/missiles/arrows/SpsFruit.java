/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.missiles.arrows;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.items.weapon.missiles.MissileWeapon;
import pd.scenes.GameScene;
import render.utils.PathFinder;

abstract class SpsFruit extends MissileWeapon {
	private final int baseMin;
	private final int baseMax;

	SpsFruit(int image, int min, int max) {
		this.image = image;
		baseMin = min;
		baseMax = max;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
	}

	protected boolean landsAt(int cell) {
		Char target = Actor.findChar(cell);
		if (target == null || target == curUser) {
			parent = null;
			return true;
		}
		return false;
	}

	protected <T extends Blob> void seed(int cell, int amount, Class<T> type) {
		if (Dungeon.level != null && Dungeon.level.insideMap(cell)) {
			GameScene.add(Blob.seed(cell, amount, type));
		}
	}

	protected <T extends Blob> void seedAround(int center, int amount, Class<T> type) {
		for (int offset : PathFinder.NEIGHBOURS8) seed(center + offset, amount, type);
	}

	@Override public int min(int lvl) { return baseMin; }
	@Override public int max(int lvl) { return baseMax; }
	@Override public int STRReq(int lvl) { return 10; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 2 * quantity(); }
}

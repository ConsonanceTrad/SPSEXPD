/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ShockWeb;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.Poison;
import pd.items.Heap;
import pd.items.keys.GoldenSkeletonKey;
import pd.levels.BossRushLevel;
import pd.scenes.GameScene;
import pd.sprites.NewDragon02Sprite;
import pd.ui.BossHealthBar;
import render.utils.Bundle;
import render.utils.Random;

/** The exact opening gatekeeper of the SPS 0.9.8 boss rush. */
public class Dragonking extends Mob {

	{
		spriteClass = NewDragon02Sprite.class;
		baseSpeed = 1f;
		HP = HT = 100;
		EXP = 1;
		defenseSkill = 0;
		properties.add(Property.BOSS);
		properties.add(Property.DRAGON);
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(0, 1);
	}

	@Override
	public int attackSkill(Char target) {
		return 1;
	}

	@Override
	public int drRoll() {
		return 0;
	}

	@Override
	public void move(int step, boolean travelling) {
		GameScene.add(Blob.seed(pos, Random.IntRange(5, 6), ShockWeb.class));
		super.move(step, travelling);
	}

	@Override
	public void die(Object cause) {
		int deathPos = pos;
		super.die(cause);
		if (Dungeon.level instanceof BossRushLevel) {
			Heap heap = Dungeon.level.drop(createLegacyKey(), deathPos);
			if (heap.sprite != null) heap.sprite.drop();
			((BossRushLevel) Dungeon.level).advance(this, UGoo.class);
		}
	}

	protected GoldenSkeletonKey createLegacyKey() {
		return new GoldenSkeletonKey(Dungeon.depth);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (state != SLEEPING) BossHealthBar.assignBoss(this);
	}
}

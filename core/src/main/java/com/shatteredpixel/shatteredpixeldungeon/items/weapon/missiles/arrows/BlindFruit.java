/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.LightEffectDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Locked;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

public class BlindFruit extends MissileWeapon {

	{
		image = ItemSpriteSheet.LEGACY_BLIND_FRUIT;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
	}

	public BlindFruit() { this(1); }
	public BlindFruit(int number) { quantity(number); }

	@Override public int min(int lvl) { return 10; }
	@Override public int max(int lvl) { return 10; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override
	protected void onThrow(int cell) {
		Char enemy = Actor.findChar(cell);
		if (enemy == null || enemy == curUser) {
			parent = null;
			GameScene.add(Blob.seed(cell, 10, ConfusionGas.class));
			for (int offset : PathFinder.NEIGHBOURS8) {
				int nearby = cell + offset;
				if (Dungeon.level.insideMap(nearby)) {
					GameScene.add(Blob.seed(nearby, 4, LightEffectDamage.class));
				}
			}
		} else {
			super.onThrow(cell);
		}
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Vertigo.class, 5f);
		Buff.prolong(defender, Silent.class, 5f);
		Buff.prolong(defender, Locked.class, 5f);
		Buff.prolong(defender, Disarm.class, 5f);
		return super.proc(attacker, defender, damage);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 10 * quantity; }
}

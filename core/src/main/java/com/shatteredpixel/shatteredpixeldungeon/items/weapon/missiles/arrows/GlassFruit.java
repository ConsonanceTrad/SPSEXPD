package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GlassFruit extends MissileWeapon {
	{
		image = ItemSpriteSheet.SEED_BLINDWEED;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 2;
		levelKnown = true;
	}

	@Override public int min(int lvl) { return 10; }
	@Override public int max(int lvl) { return 10; }

	@Override protected void onThrow(int cell) {
		Char target = Actor.findChar(cell);
		if (target == null || target == curUser) {
			GameScene.add(Blob.seed(cell, 6, CorrosiveGas.class));
		} else {
			super.onThrow(cell);
		}
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Bleeding.class).set(damage);
		return super.proc(attacker, defender, damage);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 2 * quantity; }
}

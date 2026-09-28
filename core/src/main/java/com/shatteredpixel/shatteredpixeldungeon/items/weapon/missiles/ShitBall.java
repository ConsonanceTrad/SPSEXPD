/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BeOld;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Tar;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class ShitBall extends MissileWeapon {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);

	{
		image = ItemSpriteSheet.SHIT_BALL;
		tier = 1;
		baseUses = 1;
		DLY = 0.5f;
		levelKnown = true;
	}

	public ShitBall() { quantity(2); }
	@Override public int min(int lvl) { return 1; }
	@Override public int max(int lvl) { return 1; }
	@Override public int STRReq(int lvl) { return 10; }

	@Override protected void onThrow(int cell) {
		Char target = Actor.findChar(cell);
		if (target == null) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (Dungeon.level.heroFOV[mob.pos] && Dungeon.level.distance(cell, mob.pos) <= 5) {
					Buff.affect(mob, Blindness.class, 5f);
					mob.beckon(cell);
				}
			}
		} else {
			super.onThrow(cell);
		}
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, BeOld.class).set(20f);
		Buff.affect(defender, Tar.class);
		return super.proc(attacker, defender, damage);
	}

	@Override public ShitBall random() { quantity(Random.IntRange(1, 2)); return this; }
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 5 * quantity; }
}

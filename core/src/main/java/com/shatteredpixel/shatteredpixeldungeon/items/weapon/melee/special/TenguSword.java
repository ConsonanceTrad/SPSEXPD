/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** Young Tengu's unusually fast sword. */
public class TenguSword extends MeleeWeapon {
	{
		image = ItemSpriteSheet.TENGU_SWORD;
		tier = 2;
		ACC = 1.2f;
		DLY = 0.8f;
		RCH = 1;
	}
	@Override public int min(int lvl) { return 7 + 2 * Math.max(0, lvl); }
	@Override public int max(int lvl) { return 9 + 4 * Math.max(0, lvl); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30 && damage > 1) {
			Buff.affect(defender, Bleeding.class).set(Random.IntRange(2, damage));
		}
		if (Random.Int(100) < 10 && attacker.buff(Barrier.class) == null) {
			Buff.affect(attacker, Barrier.class).setShield(damage);
		}
		return super.proc(attacker, defender, damage);
	}
}

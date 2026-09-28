/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class WraithBreath extends SpsSpecialMeleeWeapon {
	public WraithBreath() { super(2, .75f, 1f, 4, 7, 11, ItemSpriteSheet.SPS_WRAITH_BREATH); }
	@Override public int min(int level) { return 7 + Math.max(0, level) * 2; }
	@Override public int max(int level) { return 11 + Math.max(0, level) * 3; }

	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 50) {
			Buff.affect(defender, Vertigo.class, 10f);
			Buff.affect(defender, Terror.class, Terror.DURATION).object = attacker.id();
		}
		return super.proc(attacker, defender, damage);
	}
}

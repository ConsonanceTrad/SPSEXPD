/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Nunchaku extends Sai {
	{ image = ItemSpriteSheet.SAI; tier = 3; DLY = 0.8f; }
	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 15 + 3 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.prolong(defender, Daze.class, 1f);
		return super.proc(attacker, defender, damage);
	}
}

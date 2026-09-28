/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class ThornAmmo extends SpAmmo {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(5) == 3) {
			int upper = Math.max(5, damage);
			Buff.affect(defender, Bleeding.class).set(Random.IntRange(5, upper));
		} else {
			Buff.prolong(defender, Cripple.class, 3f);
		}
		defender.damage((int)(0.20f * damage), Bleeding.class);
	}
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class RotAmmo extends SpAmmo {
	private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0xCC0000);
	@Override public ItemSprite.Glowing glowing() { return RED; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 3) Buff.prolong(defender, Roots.class, 3f);
		else Buff.affect(defender, Ooze.class).set(5f);
		defender.damage((int)(0.50f * damage), attacker);
	}
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shocked;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class StormAmmo extends SpAmmo {
	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);
	@Override public ItemSprite.Glowing glowing() { return WHITE; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		if (Random.Int(6) == 3) Buff.affect(defender, Shocked.class).level(2);
		else defender.damage((int)(0.40f * damage), DamageType.SHOCK_DAMAGE);
	}
}

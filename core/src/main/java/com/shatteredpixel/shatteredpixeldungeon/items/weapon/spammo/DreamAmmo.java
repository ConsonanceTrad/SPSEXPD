/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.spammo;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.damagetype.DamageType;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class DreamAmmo extends SpAmmo {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public void onHit(Char attacker, Char defender, int damage) {
		defender.damage((int)(0.20f * damage), DamageType.DARK_DAMAGE);
		Buff.prolong(defender, ArmorBreak.class, 6f).level(25);
		Buff.prolong(defender, Slow.class, 3f);
	}
}

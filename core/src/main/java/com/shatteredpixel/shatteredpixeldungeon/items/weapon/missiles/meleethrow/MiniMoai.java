/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.meleethrow;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class MiniMoai extends MeleeThrowWeapon {
	public MiniMoai() { super(1, 10, 10, ItemSpriteSheet.SPS_MINI_MOAI); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(10) > 7) Buff.prolong(defender, Charm.class, 3f).object = attacker.id();
		return super.proc(attacker, defender, damage);
	}
	@Override public int value() { return 100; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CharmFruit extends SpsFruit {
	public CharmFruit() { this(1); }
	public CharmFruit(int number) { super(ItemSpriteSheet.SPS_SEED_DREAMFOIL, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seed(cell, 10, ParalyticGas.class);
		else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Charm.class, 10f).object = attacker.id();
		Buff.prolong(defender, Amok.class, 10f);
		return super.proc(attacker, defender, damage);
	}
}

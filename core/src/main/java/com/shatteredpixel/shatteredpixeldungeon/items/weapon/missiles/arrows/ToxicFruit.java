/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ToxicFruit extends SpsFruit {
	public ToxicFruit() { this(1); }
	public ToxicFruit(int number) { super(ItemSpriteSheet.SPS_SEED_SORROWMOSS, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) seed(cell, 8, ToxicGas.class);
		else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Poison.class).set(damage / 2f);
		return super.proc(attacker, defender, damage);
	}
}

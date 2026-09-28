/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.IceEffectDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.FrostCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostIce;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class IceFruit extends SpsFruit {
	public IceFruit() { this(1); }
	public IceFruit(int number) { super(ItemSpriteSheet.SPS_SEED_ICECAP, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, FrostCloud.class);
			seedAround(cell, 4, IceEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, FrostIce.class).level(5);
		return super.proc(attacker, defender, damage);
	}
}

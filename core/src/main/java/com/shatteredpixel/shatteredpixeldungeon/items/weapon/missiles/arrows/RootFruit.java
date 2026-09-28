/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.EarthEffectDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RootFruit extends SpsFruit {
	public RootFruit() { this(1); }
	public RootFruit(int number) { super(ItemSpriteSheet.SPS_SEED_EARTHROOT, 20, 20); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, Web.class);
			seedAround(cell, 4, EarthEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.prolong(defender, Roots.class, 8f);
		return super.proc(attacker, defender, damage);
	}
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.arrows;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.damageblobs.FireEffectDamage;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class FireFruit extends SpsFruit {
	public FireFruit() { this(1); }
	public FireFruit(int number) { super(ItemSpriteSheet.SPS_SEED_FIREBLOOM, 10, 10); quantity(number); }
	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			seed(cell, 4, com.shatteredpixel.shatteredpixeldungeon.actors.blobs.effectblobs.Fire.class);
			seedAround(cell, 4, FireEffectDamage.class);
		} else super.onThrow(cell);
	}
	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Burning.class).reignite(defender, 5f);
		return super.proc(attacker, defender, damage);
	}
}

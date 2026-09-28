/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class HorseTotem extends MiscEquippable {

	{ image = ItemSpriteSheet.SPS_HORSE_TOTEM; unique = true; }

	@Override protected MiscBuff createBuff() { return new HorseTotemBless(); }

	public boolean shouldTrigger(Hero hero) {
		return isEquipped(hero) || Random.Int(5) == 0;
	}

	public int empower(Hero hero, int damage) {
		if (damage <= 0) return damage;
		int limit = Math.max(1, damage / 3);
		int bonus = limit <= 1 ? 1 : Random.Int(1, limit);
		Buff.prolong(hero, HasteBuff.class, 4f);
		return damage + bonus;
	}

	public class HorseTotemBless extends MiscBuff { }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class BunnySpanner extends NormalMeleeWeapon {
	public BunnySpanner() {
		super(1, 1.2f, 1.5f, 2, 8, 15, ItemSpriteSheet.SPS_WEP_WAR_HAMMER);
		unique = true;
		reinforced = true;
		cursed = true;
	}
	@Override protected void applyLegacyUpgrade(Stats stats) { stats.min += 2; stats.max += 3; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.prolong(defender, Paralysis.class, 2f);
		return super.proc(attacker, defender, damage);
	}
}

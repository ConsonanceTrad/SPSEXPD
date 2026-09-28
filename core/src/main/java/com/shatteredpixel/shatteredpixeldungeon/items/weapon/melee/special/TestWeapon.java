/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

/** The tester loadout weapon, which awards experiment points on high damage rolls. */
public class TestWeapon extends NormalMeleeWeapon {

	public TestWeapon() {
		super(1, 1f, 1f, 1, 10, 10, ItemSpriteSheet.SPS_TEST_WEAPON);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (damage > Random.Int(8, 20)) {
			Hero hero = attacker instanceof Hero ? (Hero) attacker : Dungeon.hero;
			if (hero != null) hero.spp++;
		}
		return super.proc(attacker, defender, damage);
	}
}

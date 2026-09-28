/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.ranges;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.special.MeleePan;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

import java.util.ArrayList;

/** The pan's ranged form; it retains the original tier-one bow statistics. */
public class RangePan extends RangeWeapon {

	public static final String AC_CHANGE = "CHANGE";

	public RangePan() {
		super(1, Variant.NORMAL, ItemSpriteSheet.MELEE_PAN);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(100) < 30) Buff.affect(defender, Burning.class).reignite(defender, 5f);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero)) actions.add(AC_CHANGE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (AC_CHANGE.equals(action) && isEquipped(hero)) changeToMelee(hero);
		else super.execute(hero, action);
	}

	public MeleePan changeToMelee(Hero hero) {
		MeleePan replacement = new MeleePan();
		MeleePan.copyState(this, replacement);
		hero.belongings.weapon = replacement;
		return replacement;
	}

	@Override public int value() { return MeleePan.legacyValue(this); }
}

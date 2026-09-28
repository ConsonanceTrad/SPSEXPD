/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.NormalArmor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

/** Zero-defense test armor which converts every received hit into an experiment point. */
public class TestArmor extends NormalArmor {

	private static final String TYPE = "type";
	private int type;
	private TestCharge passiveBuff;

	public TestArmor() {
		super(1, 1f, 1f, 1, 0, 0, 0, 0, 0, ItemSpriteSheet.SPS_TEST_ARMOR);
	}

	@Override public int DRMin(int level) { return 0; }
	@Override public int DRMax(int level) { return 0; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender instanceof Hero) ((Hero) defender).spp++;
		return super.proc(attacker, defender, damage);
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = new TestCharge();
		passiveBuff.attachTo(ch);
	}

	@Override
	public void deactivate(Char ch) {
		super.deactivate(ch);
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = null;
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (!super.doUnequip(hero, collect, single)) return false;
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = null;
		return true;
	}

	public int type() { return type; }
	public void type(int value) { type = Math.max(0, Math.min(2, value)); }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type(bundle.getInt(TYPE));
	}

	public class TestCharge extends Buff {
		@Override public boolean act() { spend(TICK); return true; }
	}
}

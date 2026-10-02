/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** Zero-defense test armor which converts every received hit into an experiment point. */
public class TestArmor extends NormalArmor {
	{
		image = SpecificPlaceHolderDict.SPS_PH_ARMOR_TEST;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TestArmor.class)
			.t("name", "测试护甲")
			.t("desc", "测试用的护甲，每次承受攻击都会记录为一点试验点数。");
	}




	private static final String TYPE = "type";
	private int type;
	private TestCharge passiveBuff;

	public TestArmor() {
		super(1, 1f, 1f, 1, 0, 0, 0, 0, 0, SpecificPlaceHolderDict.SOMETHING_0);
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

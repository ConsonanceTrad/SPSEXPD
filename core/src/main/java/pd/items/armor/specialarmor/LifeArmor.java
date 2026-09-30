/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import render.utils.serialize.Bundle;

/** Living armor which stores recent damage as defense, then converts it to healing. */
public class LifeArmor extends NormalArmor {
	private static final String CHARGE = "charge";
	private static final String TIME = "time";
	private static final String ADAPTIVE_MAX = "adaptive_max";

	private int charge;
	private int time;
	private int adaptiveMax;
	private LifeCharge passiveBuff;

	public LifeArmor() {
		super(1, 2f, 6f, 3, 0, 0, 0, 0, 0, ItemSpriteSheet.SPS_LIFE_ARMOR);
	}

	@Override public int DRMin(int level) { return 0; }
	@Override public int DRMax(int level) { return Math.max(0, adaptiveMax); }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		charge += Math.max(0, damage);
		time = 20;
		return super.proc(attacker, defender, damage);
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (passiveBuff != null && passiveBuff.target != null) passiveBuff.detach();
		passiveBuff = new LifeCharge();
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
		charge = 0;
		time = 0;
		adaptiveMax = 0;
		return true;
	}

	public int charge() { return charge; }
	public int recoveryTime() { return time; }
	public int adaptiveMax() { return adaptiveMax; }

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(TIME, time);
		bundle.put(ADAPTIVE_MAX, adaptiveMax);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = Math.max(0, bundle.getInt(CHARGE));
		time = Math.max(0, bundle.getInt(TIME));
		adaptiveMax = Math.max(0, bundle.getInt(ADAPTIVE_MAX));
	}

	public class LifeCharge extends Buff {
		@Override
		public boolean act() {
			if (time > 1) {
				time--;
			} else {
				if (target != null) target.HP += Math.min(target.HT - target.HP, charge);
				charge = 0;
				time = 0;
			}
			adaptiveMax = charge >= adaptiveMax ? charge : 0;
			spend(TICK);
			return true;
		}

		public void tickNow() { act(); }
	}
}

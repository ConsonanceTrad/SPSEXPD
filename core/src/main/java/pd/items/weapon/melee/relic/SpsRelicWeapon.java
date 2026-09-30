package pd.items.weapon.melee.relic;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.weapon.melee.MeleeWeapon;
import watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class SpsRelicWeapon extends MeleeWeapon {

	private static final String CHARGE = "charge";
	public static final int CHARGE_CAP = 1000;

	public int charge;
	private Buff chargeBuff;
	private final int legacyMax;

	protected SpsRelicWeapon(float accuracy, float delay, int reach) {
		tier = 6;
		reinforced = true;
		ACC = accuracy;
		DLY = delay;
		RCH = reach;
		legacyMax = (int) (((tier * tier - tier + 10) / accuracy * delay)
				/ (0.8f + 0.2f * reach));
	}

	protected abstract String relicAction();

	protected abstract void useRelicPower(Hero hero);

	@Override
	public int min(int lvl) {
		return tier + 2 * lvl;
	}

	@Override
	public int max(int lvl) {
		return legacyMax + 4 * lvl;
	}

	@Override
	public int STRReq(int lvl) {
		return 20;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (isEquipped(hero) && charge >= CHARGE_CAP) actions.add(relicAction());
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (relicAction().equals(action) && isEquipped(hero) && charge >= CHARGE_CAP) {
			charge = 0;
			useRelicPower(hero);
			updateQuickslot();
		} else {
			super.execute(hero, action);
		}
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);
		if (ch instanceof Hero) {
			chargeBuff = new RelicCharge();
			chargeBuff.attachTo(ch);
		}
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (!super.doUnequip(hero, collect, single)) return false;
		if (chargeBuff != null) chargeBuff.detach();
		chargeBuff = null;
		charge = 0;
		return true;
	}

	@Override
	public String status() {
		return charge + "/" + CHARGE_CAP;
	}

	@Override
	public Item upgrade(boolean enchant) {
		Enchantment relicEnchantment = enchantment;
		Item result = super.upgrade(false);
		if (relicEnchantment != null) enchant(relicEnchantment);
		return result;
	}

	@Override
	public int value() {
		int price = enchantment == null ? 300 : 450;
		if (cursed && cursedKnown) price /= 2;
		if (levelKnown) {
			if (level() > 0) price *= level() + 1;
			else if (level() < 0) price /= 1 - level();
		}
		return Math.max(1, price);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt(CHARGE);
	}

	private class RelicCharge extends Buff {
		@Override
		public boolean act() {
			if (charge < CHARGE_CAP) {
				charge = Math.min(CHARGE_CAP, charge + Math.min(Math.max(level(), 0), 10));
				updateQuickslot();
			}
			spend(TICK);
			return true;
		}
	}
}

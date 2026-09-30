/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.melee.start;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Vertigo;
import pd.actors.mobs.Mob;
import pd.items.Heap;
import pd.items.Item;
import pd.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import pd.items.wands.fusion.WandOfFlow;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import render.utils.Bundle;

public class Whisk extends NormalMeleeWeapon {

	private static final String CHARGE = "charge";
	private static final String EXTRA_CHARGE = "extra_charge";
	private int charge;
	private int extraCharge;

	public Whisk() {
		super(3, 1f, 1f, 2, 8, 15, ItemSpriteSheet.SPS_WHISK);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		charge++;
		if (charge > 10) {
			int opposite = defender.pos + defender.pos - attacker.pos;
			WandOfFlow.throwChar(defender,
					new Ballistica(defender.pos, opposite, Ballistica.MAGIC_BOLT), 1);
			Buff.prolong(defender, Vertigo.class, 3f);
			charge = 0;
			extraCharge++;
		}

		if (extraCharge > 4 && defender instanceof Mob) {
			Mob mob = (Mob) defender;
			Item loot = mob.SupercreateLoot();
			if (loot != null && Dungeon.level != null && Dungeon.level.insideMap(defender.pos)) {
				Heap heap = Dungeon.level.drop(loot, defender.pos);
				if (heap.sprite != null) heap.sprite.drop();
			}
			mob.firstItem = false;
			extraCharge = 0;
		}
		updateQuickslot();
		return result;
	}

	public int charge() { return charge; }
	public int extraCharge() { return extraCharge; }
	public void charge(int normal, int extra) {
		charge = Math.max(0, normal);
		extraCharge = Math.max(0, extra);
		updateQuickslot();
	}

	@Override public String info() {
		return super.info() + "\n\n" + Messages.get(this, "charge", charge, 10)
				+ "\n\n" + Messages.get(this, "chargeex", extraCharge, 5);
	}

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		bundle.put(EXTRA_CHARGE, extraCharge);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge(bundle.getInt(CHARGE), bundle.getInt(EXTRA_CHARGE));
	}
}

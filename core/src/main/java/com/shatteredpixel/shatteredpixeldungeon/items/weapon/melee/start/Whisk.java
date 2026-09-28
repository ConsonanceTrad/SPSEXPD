/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.start;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.normalweapon.NormalMeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.fusion.WandOfFlow;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

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

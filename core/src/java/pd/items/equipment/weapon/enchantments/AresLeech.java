/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.mobs.Mob;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.melee.relic.SpsRelicWeapon;
import pd.messages.Messages;
import pd.sprites.ItemSprite;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

/** Converts an Ares relic hit into delayed healing and nearby-soul charge. */
public class AresLeech extends Weapon.Enchantment {
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (!(weapon instanceof SpsRelicWeapon) || Dungeon.level == null) return damage;
		SpsRelicWeapon relic = (SpsRelicWeapon)weapon;
		int level = Math.max(0, weapon.level());
		int maximum = damage * (level + 2) / (level + 6);
		int effective = Math.min(Random.IntRange(0, Math.max(0, maximum)), attacker.HT - attacker.HP);
		for (Mob mob : Dungeon.level.mobs()) {
			if (Dungeon.level.distance(attacker.pos, mob.pos) < 3 && mob.isAlive() && effective < mob.HP) {
				relic.charge = Math.min(SpsRelicWeapon.CHARGE_CAP, relic.charge + 1);
			}
		}
		if (effective > 0) Buff.affect(attacker, HealDamage.class).prolong(damage);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x660066); }

	public static class HealDamage extends Buff {
		private static final String DAMAGE = "damage";
		private int damage;
		public void prolong(int value) { damage = value; }
		public int remaining() { return damage; }
		@Override public boolean act() {
			if (!target.isAlive() || damage <= 0 || target.HP >= target.HT) {
				detach();
				return true;
			}
			int healing = Math.max(1, (int)(damage * 0.1f));
			target.HP = Math.min(target.HT, target.HP + healing);
			damage -= healing;
			if (--damage <= 0 || target.HP >= target.HT) detach();
			else spend(TICK);
			return true;
		}
		@Override public int icon() { return BuffIndicator.HEALING; }
		@Override public String desc() { return Messages.get(this, "desc", damage); }
		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DAMAGE, damage);
		}
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			damage = bundle.getInt(DAMAGE);
		}
	}
}

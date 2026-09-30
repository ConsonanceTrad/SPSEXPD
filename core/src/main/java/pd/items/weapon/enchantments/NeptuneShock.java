/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.weapon.enchantments;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.damagetype.DamageType;
import pd.items.weapon.Weapon;
import pd.items.weapon.melee.relic.SpsRelicWeapon;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import java.util.HashSet;

/** Spends ten relic charge to shock a random chain of adjacent creatures. */
public class NeptuneShock extends Weapon.Enchantment {
	public static final int CHARGE_COST = 10;
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		if (!(weapon instanceof SpsRelicWeapon)) return damage;
		SpsRelicWeapon relic = (SpsRelicWeapon)weapon;
		if (relic.charge < CHARGE_COST) return damage;
		relic.charge -= CHARGE_COST;
		int level = Math.max(0, weapon.level());
		if (Random.Int(level + 4) < 3) return damage;
		HashSet<Char> affected = new HashSet<>();
		affected.add(attacker);
		int minimum = Math.max(1, damage / 3);
		int maximum = Math.max(minimum, damage / 2);
		shock(defender, Random.IntRange(minimum, maximum), affected);
		return damage;
	}
	private void shock(Char target, int damage, HashSet<Char> affected) {
		if (target == null || damage < 1 || Dungeon.level == null || !affected.add(target)) return;
		int dealt = Dungeon.level.water[target.pos] && !target.flying ? damage * 2 : damage;
		target.damage(dealt, DamageType.SHOCK_DAMAGE);
		HashSet<Char> neighbours = new HashSet<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char next = Actor.findChar(target.pos + offset);
			if (next != null && !affected.contains(next)) neighbours.add(next);
		}
		if (!neighbours.isEmpty()) shock(Random.element(neighbours), Random.IntRange(Math.max(1, damage / 2), damage), affected);
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x66CCFF); }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Lightning;
import pd.effects.particles.SparkParticle;
import pd.items.equipment.weapon.Weapon;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import java.util.ArrayList;
import java.util.HashSet;

import static pd.actors.damagetype.DamageType.SHOCK_DAMAGE;

public class EnchantmentShock extends SpsEnchantment {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x00FF00);
	private final ArrayList<Char> affected = new ArrayList<>();

	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = legacyLevel(attacker);
		if (Random.Int(level + 4) >= 3) {
			affected.clear();
			affected.add(attacker);
			hit(defender, (int)(legacyRoll(weapon, attacker) * 0.75f));
			if (hasClover(attacker)) {
				defender.damage((int)(legacyRoll(weapon, attacker) * 0.50f), SHOCK_DAMAGE);
			}
			if (attacker.sprite != null && attacker.sprite.parent != null && defender.sprite != null) {
				attacker.sprite.parent.add(new Lightning(attacker.pos, defender.pos, null));
			}
		}
		return damage;
	}

	private void hit(Char target, int damage) {
		if (damage < 1) return;
		affected.add(target);
		boolean wet = Dungeon.level != null && target.pos >= 0 && target.pos < Dungeon.level.length()
				&& Dungeon.level.water[target.pos] && !target.flying;
		target.damage(wet ? damage * 2 : damage, SHOCK_DAMAGE);
		if (target.sprite != null) {
			target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
			target.sprite.flash();
		}
		if (Dungeon.level == null) return;
		HashSet<Char> neighbours = new HashSet<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = target.pos + offset;
			if (cell < 0 || cell >= Dungeon.level.length() || !Dungeon.level.adjacent(target.pos, cell)) continue;
			Char neighbour = Actor.findChar(cell);
			if (neighbour != null && !affected.contains(neighbour)) neighbours.add(neighbour);
		}
		if (!neighbours.isEmpty()) hit(Random.element(neighbours), Random.Int(damage / 2, damage));
	}

	@Override public ItemSprite.Glowing glowing() { return GREEN; }
}

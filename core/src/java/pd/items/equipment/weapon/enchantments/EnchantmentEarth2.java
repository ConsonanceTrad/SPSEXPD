/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Roots;
import pd.effects.CellEmitter;
import pd.effects.particles.EarthParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.EARTH_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentEarth2 extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentEarth2.class)
			.t("name", "酸蚀%s")
			.t("desc", "酸蚀附魔将造成少量的地属性伤害，并有几率给目标上腐酸。");
	}

	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0x996600);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, EARTH_DAMAGE);
		if (Random.Int(4) == 1 && defender.isAlive()) {
			Buff.prolong(defender, Roots.class, 3f);
			Buff.affect(defender, Ooze.class).set(Math.max(0, weapon.level()));
			if (defender.sprite != null) CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.effects.CellEmitter;
import pd.effects.particles.EarthParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.EARTH_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentEarth extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentEarth.class)
			.t("name", "自然%s")
			.t("desc", "自然附魔将造成大量的地属性伤害，并有几率对目标埋下寄生种子。");
	}

	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0x996600);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.75f, EARTH_DAMAGE);
		if (Random.Int(10) == 1) {
			Buff.affect(defender, GrowSeed.class).set(3f);
			if (defender.sprite != null) CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
}

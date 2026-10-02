/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cold;
import pd.actors.buffs.Frost;
import pd.actors.buffs.Wet;
import pd.effects.particles.SnowParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.ICE_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentIce extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentIce.class)
			.t("name", "寒潮%s")
			.t("desc", "寒潮将造成大量的冰属性伤害，并有几率给目标施加寒冷和潮湿效果。");
	}

	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0044FF);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.75f, ICE_DAMAGE);
		Buff.prolong(defender, Wet.class, 3f);
		Buff.prolong(defender, Cold.class, 3f);
		if (Random.Int(3) == 1) {
			Buff.affect(defender, Frost.class, 5f * Random.Float(2f, 4f));
			if (defender.sprite != null) defender.sprite.emitter().burst(SnowParticle.FACTORY, 5);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
}

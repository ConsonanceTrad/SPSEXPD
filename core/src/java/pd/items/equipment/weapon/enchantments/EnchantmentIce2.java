/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FrostIce;
import pd.effects.particles.SnowParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.ICE_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentIce2 extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentIce2.class)
			.t("name", "冰霜%s")
			.t("desc", "冰霜附魔将造成少量的冰属性伤害，并几率冻伤目标。");
	}



	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0044FF);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, ICE_DAMAGE);
		if (Random.Int(3) >= 1) Buff.affect(defender, FrostIce.class).level(legacyRoll(weapon, attacker));
		if (defender.sprite != null) defender.sprite.emitter().burst(SnowParticle.FACTORY, 5);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
}

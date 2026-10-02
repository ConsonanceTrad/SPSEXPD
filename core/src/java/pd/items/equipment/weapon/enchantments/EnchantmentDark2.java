/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShadowCurse;
import pd.effects.particles.ShadowParticle;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;

import static pd.actors.damagetype.DamageType.DARK_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentDark2 extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentDark2.class)
			.t("name", "咒术%s")
			.t("desc", "咒术附魔将造成少量的暗属性伤害，并有几率咒杀目标。");
	}

	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		Buff.affect(defender, ShadowCurse.class);
		elementalDamage(weapon, attacker, defender, 0.25f, DARK_DAMAGE);
		if (defender.sprite != null) defender.sprite.emitter().burst(ShadowParticle.UP, 5);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return BLACK; }
}

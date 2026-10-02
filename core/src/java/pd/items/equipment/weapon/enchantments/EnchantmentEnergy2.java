/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Vertigo;
import pd.effects.Speck;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;

import static pd.actors.damagetype.DamageType.ENERGY_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentEnergy2 extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentEnergy2.class)
			.t("name", "剑舞%s")
			.t("desc", "剑舞附魔将造成大量的无属性伤害，并给使用者提供物理护盾。");
	}



	private static final ItemSprite.Glowing GRAY = new ItemSprite.Glowing(0x888888);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.75f, ENERGY_DAMAGE);
		if (attacker.buff(ShieldArmor.class) == null) {
			Buff.affect(attacker, ShieldArmor.class).level(legacyRoll(weapon, attacker));
		} else {
			Buff.prolong(defender, Vertigo.class, 3f);
		}
		if (attacker.sprite != null) attacker.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return GRAY; }
}

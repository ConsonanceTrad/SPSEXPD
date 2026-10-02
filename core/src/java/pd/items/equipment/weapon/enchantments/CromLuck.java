/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

/** Re-rolls the complete attack damage and applies the best excess roll. */
public class CromLuck extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(CromLuck.class)
			.t("name", "锯骨%s")
			.t("desc", "锯骨附魔会反复尝试更高的伤害，并无视防御补上差值。");
	}

	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int best = damage;
		for (int i = 0; i <= Math.max(0, weapon.level()); i++) best = Math.max(best, attacker.damageRoll());
		if (best > damage) defender.damage(best - damage, weapon);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x800000); }
}

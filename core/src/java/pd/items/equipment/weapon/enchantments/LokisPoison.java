/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** Applies the stronger SPS relic poison. */
public class LokisPoison extends Weapon.Enchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LokisPoison.class)
			.t("name", "猛毒%s")
			.t("desc", "猛毒附魔能给目标施加更强的毒素。");
	}



	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		int level = Math.max(0, weapon.level());
		if (Random.Int(level + 3) >= 2) {
			Buff.affect(defender, pd.actors.buffs.LokisPoison.class)
					.set(level + 1f);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return new ItemSprite.Glowing(0x4400AA); }
}

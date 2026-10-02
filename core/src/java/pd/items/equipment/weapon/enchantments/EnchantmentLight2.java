/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.enchantments;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.LightShootAttack;
import pd.effects.Speck;
import pd.items.equipment.weapon.Weapon;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

import static pd.actors.damagetype.DamageType.LIGHT_DAMAGE;
import pd.messages.InlineText;

public class EnchantmentLight2 extends SpsEnchantment {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(EnchantmentLight2.class)
			.t("name", "圣光%s")
			.t("desc", "圣光附魔将造成少量的光属性伤害，并有几率以圣光打击目标。");
	}

	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	@Override public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
		elementalDamage(weapon, attacker, defender, 0.25f, LIGHT_DAMAGE);
		if (Random.Int(3) == 1) Buff.affect(defender, LightShootAttack.class).level(5);
		if (defender.sprite != null) defender.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
}

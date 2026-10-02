/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.armorbuff.GlyphElectricity;
import pd.actors.hero.Hero;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Electricityglyph extends SpsGlyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Electricityglyph.class)
			.t("name", "电网%s")
			.t("desc", "电网刻印可以增加使用者的雷电抗性，并有几率麻痹攻击者或为使用者充能。");
	}

	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xFFFFFF);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphElectricity.class);
		int level = level(armor);
		if (defender instanceof Hero && level > 0 && Random.Int(level) >= 5) {
			Buff.prolong(defender, Recharging.class, Math.min(level, 30));
		}
		if (attacker != null && roll(level + 6, 5, defender, 3)) Buff.prolong(attacker, Paralysis.class, 2f);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

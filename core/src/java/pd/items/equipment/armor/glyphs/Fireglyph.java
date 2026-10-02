/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.armorbuff.GlyphFire;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Fireglyph extends SpsGlyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Fireglyph.class)
			.t("name", "火罩%s")
			.t("desc", "火罩刻印可以增加使用者的火焰抗性，并有几率点燃攻击者或提升使用者的攻击力。");
	}



	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xFF4400);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphFire.class);
		int level = level(armor);
		if (attacker != null && roll(level + 6, 5, defender, 3)) {
			Buff.affect(attacker, Burning.class).reignite(attacker, 5f);
		}
		if (Random.Int(level + 7) >= 6) Buff.prolong(defender, AttackUp.class, 5f).level(25);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

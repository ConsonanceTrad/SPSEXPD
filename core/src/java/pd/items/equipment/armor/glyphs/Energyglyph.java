/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.armorbuff.GlyphEnergy;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class Energyglyph extends SpsGlyph {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Energyglyph.class)
			.t("name", "缓冲%s")
			.t("desc", "缓冲刻印将提升使用者的能量伤害抗性。");
	}



	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x330033);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphEnergy.class);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

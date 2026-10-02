/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.armorbuff.GlyphEarth;
import pd.items.equipment.armor.Armor;
import pd.plants.Earthroot;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class Earthglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xCCCCCC);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphEarth.class);
		int level = level(armor);
		if (Random.Int(4) == 0) Buff.affect(defender, Earthroot.Armor.class).level(5 * (level + 1));
		if (roll(level + 6, 5, defender, 3)) Buff.prolong(defender, DefenceUp.class, 5f).level(20);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

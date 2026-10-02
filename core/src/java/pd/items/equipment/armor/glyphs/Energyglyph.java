/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.armorbuff.GlyphEnergy;
import pd.items.equipment.armor.Armor;
import pd.sprites.ItemSprite;

public class Energyglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x330033);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphEnergy.class);
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

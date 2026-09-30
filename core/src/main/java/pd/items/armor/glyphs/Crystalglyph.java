/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.DelayProtect;
import pd.items.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.Random;

public class Crystalglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xCCAA88);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		clearElementalMarker(defender);
		int level = level(armor);
		if (Random.Int(level / 2 + 6) >= 5 && (damage > 30 || lucky(defender) && damage > 15)) {
			Buff.affect(defender, DelayProtect.class);
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

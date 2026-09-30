/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.armorbuff.GlyphElectricity;
import pd.actors.hero.Hero;
import pd.items.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class Electricityglyph extends SpsGlyph {
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

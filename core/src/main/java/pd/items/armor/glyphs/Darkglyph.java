/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.armorbuff.GlyphDark;
import pd.actors.damagetype.DamageType;
import pd.items.armor.Armor;
import pd.sprites.ItemSprite;
import render.utils.math.Random;

public class Darkglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0x000000);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphDark.class);
		if (attacker == null || attacker.HP <= 0) return damage;
		int level = level(armor);
		if (roll(level / 2 + 5, 8, defender, 6)) {
			int healingBound = attacker.HP / 10;
			if (healingBound > 0) {
				int harm = Random.Int(healingBound);
				int healBound = Math.min(harm, Math.max(0, defender.HT - defender.HP) / 4) / 2;
				if (healBound > 0) defender.HP = Math.min(defender.HT, defender.HP + Random.Int(healBound));
				if (harm > 0) attacker.damage(harm, DamageType.DARK_DAMAGE);
			}
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

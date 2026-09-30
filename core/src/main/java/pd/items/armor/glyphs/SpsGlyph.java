/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.glyphs;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.armorbuff.ArmorGlyphBuff;
import pd.items.armor.Armor;
import pd.items.misc.FourClover;
import com.watabou.utils.Random;

abstract class SpsGlyph extends Armor.Glyph {
	static int level(Armor armor) { return Math.max(0, armor.level()); }
	static boolean lucky(Char defender) { return defender.buff(FourClover.FourCloverBless.class) != null; }
	static boolean roll(int bound, int threshold, Char defender, int luckyThreshold) {
		return Random.Int(Math.max(1, bound)) >= threshold
				|| lucky(defender) && Random.Int(Math.max(1, bound)) >= luckyThreshold;
	}
	static void clearElementalMarker(Char defender) { Buff.detach(defender, ArmorGlyphBuff.class); }
	static <T extends ArmorGlyphBuff> void setElementalMarker(Char defender, Class<T> marker) {
		if (defender.isAlive() && defender.buff(marker) == null) {
			clearElementalMarker(defender);
			Buff.affect(defender, marker);
		}
	}
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff.GlyphLight;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class Lightglyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xFFFF44);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		setElementalMarker(defender, GlyphLight.class);
		if (attacker == null) return damage;
		int level = (int)GameMath.gate(0, armor.level(), 6);
		int bound = level / 2 + 5;
		if (Random.Int(bound) >= 4) {
			Buff.affect(attacker, Charm.class, Random.IntRange(4, 7)).object = defender.id();
			Buff.affect(attacker, Amok.class, 10f);
		} else if (Random.Int(bound) >= 3 || lucky(defender) && Random.Int(bound) >= 1) {
			Buff.affect(attacker, Terror.class, 10f).object = defender.id();
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

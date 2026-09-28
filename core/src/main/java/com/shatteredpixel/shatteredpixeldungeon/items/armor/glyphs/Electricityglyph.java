/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff.GlyphElectricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

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

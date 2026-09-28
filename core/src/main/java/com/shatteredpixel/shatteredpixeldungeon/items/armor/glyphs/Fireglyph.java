/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.armorbuff.GlyphFire;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Fireglyph extends SpsGlyph {
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

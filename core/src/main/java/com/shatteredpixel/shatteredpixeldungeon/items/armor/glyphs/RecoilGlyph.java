/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.fusion.WandOfFlow;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class RecoilGlyph extends SpsGlyph {
	private static final ItemSprite.Glowing COLOR = new ItemSprite.Glowing(0xCC6600);
	@Override public int proc(Armor armor, Char attacker, Char defender, int damage) {
		clearElementalMarker(defender);
		if (attacker == null) return damage;
		int level = level(armor);
		if (Dungeon.level != null && roll(level + 5, 4, defender, 2)) {
			int opposite = attacker.pos + attacker.pos - defender.pos;
			WandOfFlow.throwChar(attacker, new Ballistica(attacker.pos, opposite, Ballistica.MAGIC_BOLT), 2);
		}
		if (Random.Int(level / 2 + 5) >= 4) {
			Buff.affect(attacker, Bleeding.class).set(Math.max(level / 2, damage));
		}
		return damage;
	}
	@Override public ItemSprite.Glowing glowing() { return COLOR; }
}

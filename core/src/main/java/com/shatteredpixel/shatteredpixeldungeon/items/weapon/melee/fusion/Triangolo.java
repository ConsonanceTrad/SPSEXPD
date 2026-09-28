/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

public class Triangolo extends Shortsword {
	{ image = ItemSpriteSheet.SAI; tier = 1; }
	@Override public int min(int lvl) { return 2 + lvl; }
	@Override public int max(int lvl) { return 7 + 2 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char ch = Actor.findChar(defender.pos + offset);
			if (ch != null && ch != attacker && ch != defender && ch.alignment != attacker.alignment) {
				ch.damage(Math.max(1, result / 4), this);
			}
		}
		return result;
	}
}

/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.fusion;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.PathFinder;

public class Flute extends Mace implements FusionWeapon {
	{ image = ItemSpriteSheet.WAND_REGROWTH; tier = 2; ACC = 1.05f; }
	@Override public int min(int lvl) { return 3 + lvl; }
	@Override public int max(int lvl) { return 12 + 3 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char ch = Actor.findChar(defender.pos + offset);
			if (ch != null && ch != attacker && ch != defender && ch.alignment != attacker.alignment) {
				ch.damage(Math.max(1, result / 5), this);
			}
		}
		return result;
	}
}

/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.weapon.melee.fusion;

import pd.actors.Actor;
import pd.actors.Char;
import pd.items.weapon.melee.WarHammer;
import pd.sprites.ItemSpriteSheet;
import render.utils.PathFinder;

public class Trumpet extends WarHammer implements FusionWeapon {
	{ image = ItemSpriteSheet.WAR_HAMMER; tier = 4; ACC = 0.95f; }
	@Override public int min(int lvl) { return 5 + lvl; }
	@Override public int max(int lvl) { return 22 + 5 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char ch = Actor.findChar(defender.pos + offset);
			if (ch != null && ch != attacker && ch != defender && ch.alignment != attacker.alignment) {
				ch.damage(Math.max(1, result / 6), this);
			}
		}
		return result;
	}
}

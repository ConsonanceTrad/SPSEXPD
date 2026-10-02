/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.equipment.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Actor;
import pd.actors.Char;
import pd.items.equipment.weapon.melee.WarHammer;
import pd.mechanics.pathfind.PathFinder;

public class Trumpet extends WarHammer implements FusionWeapon {
	{ image = EquipmentEquipWeaponBasicWeaponDict.WAR_HAMMER_0; tier = 4; ACC = 0.95f; }
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

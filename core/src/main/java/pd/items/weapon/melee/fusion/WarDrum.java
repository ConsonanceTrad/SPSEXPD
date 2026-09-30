/*
 * Content adapted from Special Surprise Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.weapon.melee.fusion;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.items.weapon.melee.Mace;
import pd.mechanics.pathfind.PathFinder;
import pd.sprites.ItemSpriteSheet;

public class WarDrum extends Mace implements FusionWeapon {

	{
		image = ItemSpriteSheet.WAR_HAMMER;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 0.8f;
		tier = 4;
		ACC = 1f;
	}

	@Override
	public int max(int lvl) {
		return 20 + 5 * lvl;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int result = super.proc(attacker, defender, damage);
		for (int offset : PathFinder.NEIGHBOURS8) {
			Char target = Actor.findChar(defender.pos + offset);
			if (target != null && target != attacker && target != defender
					&& target.alignment != attacker.alignment) {
				target.damage(Math.max(1, result / 4), this);
			}
		}
		return result;
	}
}

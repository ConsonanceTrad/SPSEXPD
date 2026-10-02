/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.weapon.melee.fusion;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Daze;
import pd.items.weapon.melee.Whip;
import render.utils.math.Random;

public class ReedPipe extends Whip implements FusionWeapon {

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.35f;
		tier = 2;
	}

	@Override
	public int min(int lvl) {
		return 2 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 10 + 3 * lvl;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(7) == 0) {
			Buff.prolong(defender, Daze.class, 2f);
		}
		return super.proc(attacker, defender, damage);
	}
}

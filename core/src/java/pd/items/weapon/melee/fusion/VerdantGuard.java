/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.actors.Char;
import pd.items.weapon.melee.Quarterstaff;

public class VerdantGuard extends Quarterstaff implements FusionWeapon {

	{
		image = EquipmentEquipWeaponBasicWeaponDict.ROUND_SHIELD_0;
		hitSound = Assets.Sounds.HIT_CRUSH;
		tier = 3;
	}

	@Override
	public int max(int lvl) {
		return 14 + 4 * lvl;
	}

	@Override
	public int defenseFactor(Char owner) {
		return 3 + Math.max(0, buffedLvl()) / 2;
	}
}

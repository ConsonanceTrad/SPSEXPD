/*
 * Content adapted from Magic Ling Pixel Dungeon for Shattered Pixel Dungeon 4.0.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.items.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.Assets;
import pd.items.weapon.melee.Sword;

public class RitualBlade extends Sword implements FusionWeapon {

	{
		image = EquipmentEquipWeaponBasicWeaponDict.SICKLE_0;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.15f;
		tier = 2;
		ACC = 1.1f;
	}

	@Override
	public int min(int lvl) {
		return 3 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 13 + 3 * lvl;
	}
}

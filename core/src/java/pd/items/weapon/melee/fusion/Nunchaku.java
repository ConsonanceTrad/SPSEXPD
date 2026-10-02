/* Special Surprise content rebuilt for Shattered Pixel Dungeon 4.0. GPLv3+. */
package pd.items.weapon.melee.fusion;

import pd.atlas.items.EquipmentEquipWeaponBasicWeaponDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Daze;
import pd.items.weapon.melee.Sai;
import render.utils.math.Random;

public class Nunchaku extends Sai implements FusionWeapon {
	{ image = EquipmentEquipWeaponBasicWeaponDict.SAI_0; tier = 3; DLY = 0.8f; }
	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 15 + 3 * lvl; }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.prolong(defender, Daze.class, 1f);
		return super.proc(attacker, defender, damage);
	}
}

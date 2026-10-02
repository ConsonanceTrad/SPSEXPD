/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;

public class AsceticArmor extends NormalArmor {
	public AsceticArmor() { super(3, 3.5f, 11f, 4, 0, 15, -1, 1, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.affect(defender, HasteBuff.class, 10f);
		return super.proc(attacker, defender, damage);
	}
}

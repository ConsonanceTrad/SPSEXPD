/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.items.equipment.armor.normalarmor.NormalArmor;
import render.utils.math.Random;

public class WarriorArmor extends NormalArmor {
	public WarriorArmor() { super(7, 1f, 1f, 2, 20, 40, 0, 3, 5, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.affect(defender, MagicArmor.class).level(damage);
		return super.proc(attacker, defender, damage);
	}
}

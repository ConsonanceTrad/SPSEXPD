/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.items.armor.normalarmor.NormalArmor;
import render.utils.math.Random;

public class FollowerArmor extends NormalArmor {
	public FollowerArmor() { super(4, 3.5f, 10f, 5, 0, 20, -1, 1, 3, SpecificPlaceHolderDict.SOMETHING_0); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) defender.HP = Math.min(defender.HT, defender.HP + Math.max(0, damage / 4));
		return super.proc(attacker, defender, damage);
	}
}

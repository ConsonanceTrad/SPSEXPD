/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class WarriorArmor extends NormalArmor {
	public WarriorArmor() { super(7, 1f, 1f, 2, 20, 40, 0, 3, 5, ItemSpriteSheet.SPS_ARMOR_WARRIOR); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.affect(defender, MagicArmor.class).level(damage);
		return super.proc(attacker, defender, damage);
	}
}

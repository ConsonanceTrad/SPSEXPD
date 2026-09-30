/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import render.utils.math.Random;

public class AsceticArmor extends NormalArmor {
	public AsceticArmor() { super(3, 3.5f, 11f, 4, 0, 15, -1, 1, 3, ItemSpriteSheet.SPS_ARMOR_ASCETIC); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.affect(defender, HasteBuff.class, 10f);
		return super.proc(attacker, defender, damage);
	}
}

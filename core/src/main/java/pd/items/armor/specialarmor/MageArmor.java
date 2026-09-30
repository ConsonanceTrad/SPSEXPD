/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class MageArmor extends NormalArmor {
	public MageArmor() { super(1, 3f, 7f, 4, 0, 4, 0, 1, 4, ItemSpriteSheet.SPS_ARMOR_MAGE); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		return super.proc(attacker, defender, Random.Int(8) == 0 ? 0 : damage);
	}
}

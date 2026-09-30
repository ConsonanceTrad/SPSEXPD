/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.actors.Char;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Random;

public class HuntressArmor extends NormalArmor {
	public HuntressArmor() { super(2, 2.4f, 6f, 4, 0, 12, 0, 1, 3, ItemSpriteSheet.SPS_ARMOR_HUNTRESS); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (attacker != null && Random.Int(8) == 0) attacker.damage(Math.max(0, damage), defender);
		return super.proc(attacker, defender, damage);
	}
}

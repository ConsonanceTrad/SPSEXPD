/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.armor.specialarmor;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.armor.normalarmor.NormalArmor;
import pd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class RogueArmor extends NormalArmor {
	public RogueArmor() { super(1, 5f, 13f, 2, 0, 2, -1, 1, 3, ItemSpriteSheet.SPS_ARMOR_ROGUE); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Dungeon.gold = Math.max(0, Dungeon.gold + Math.max(0, damage));
		return super.proc(attacker, defender, damage);
	}
}

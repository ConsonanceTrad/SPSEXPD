/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.NormalArmor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class MageArmor extends NormalArmor {
	public MageArmor() { super(1, 3f, 7f, 4, 0, 4, 0, 1, 4, ItemSpriteSheet.SPS_ARMOR_MAGE); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		return super.proc(attacker, defender, Random.Int(8) == 0 ? 0 : damage);
	}
}

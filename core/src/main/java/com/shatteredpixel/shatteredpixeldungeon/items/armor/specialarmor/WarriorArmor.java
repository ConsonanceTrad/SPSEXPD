/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.NormalArmor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class WarriorArmor extends NormalArmor {
	public WarriorArmor() { super(7, 1f, 1f, 2, 20, 40, 0, 3, 5, ItemSpriteSheet.SPS_ARMOR_WARRIOR); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) Buff.affect(defender, MagicArmor.class).level(damage);
		return super.proc(attacker, defender, damage);
	}
}

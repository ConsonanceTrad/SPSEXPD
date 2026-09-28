/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.armor.specialarmor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TargetShoot;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.normalarmor.NormalArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.guns.GunWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class SoldierArmor extends NormalArmor {
	public SoldierArmor() { super(5, 1f, 1f, 2, 20, 40, 1, 0, 3, ItemSpriteSheet.SPS_ARMOR_SOLDIER); }
	@Override public int proc(Char attacker, Char defender, int damage) {
		if (Random.Int(8) == 0) {
			if (defender instanceof Hero) {
				for (Item item : ((Hero)defender).belongings) if (item instanceof GunWeapon) ((GunWeapon)item).addRound();
			}
			Buff.affect(defender, TargetShoot.class, 10f);
		}
		return super.proc(attacker, defender, damage);
	}
}

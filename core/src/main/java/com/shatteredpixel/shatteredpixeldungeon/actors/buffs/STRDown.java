/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

/** Legacy SPS weakening magic: -3 effective strength and one charge drained from every wand. */
public class STRDown extends FlavourBuff {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean attachTo(Char target) {
		if (!(target instanceof Hero) || !super.attachTo(target)) return false;
		Hero hero = (Hero) target;
		for (Item item : hero.belongings) {
			if (item instanceof Wand) {
				Wand wand = (Wand) item;
				if (wand.curCharges > 0) {
					wand.curCharges--;
					wand.updateQuickslot();
				}
			}
		}
		return true;
	}

	@Override public int icon() { return BuffIndicator.WEAKNESS; }
}

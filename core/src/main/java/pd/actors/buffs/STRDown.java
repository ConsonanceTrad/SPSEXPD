/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.wands.Wand;
import pd.ui.BuffIndicator;

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

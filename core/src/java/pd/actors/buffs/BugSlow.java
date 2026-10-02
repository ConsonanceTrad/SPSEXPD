/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.Dungeon;
import pd.actors.hero.Hero;
import pd.items.consum.food.BugMeat;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class BugSlow extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BugSlow.class)
			.t("name", "稽生")
			.t("desc", "宿主会被寄生虫周期性减速。清空背包中的寄生虫即可解除此效果。");
	}




	private static final String DELAY = "slow_delay";
	private int slowDelay;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public boolean act() {
		if (!target.isAlive()) {
			detach();
			return true;
		}
		if (++slowDelay > 8) {
			slowDelay = 0;
			if (target instanceof Hero
					&& Dungeon.hero.belongings.getItem(BugMeat.class) == null) {
				detach();
			} else {
				Buff.prolong(target, Slow.class, 2f);
			}
		}
		spend(TICK);
		return true;
	}

	public void dispel() {
		detach();
	}

	@Override public int icon() { return BuffIndicator.VERTIGO; }
	@Override public String desc() { return Messages.get(this, "desc"); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(DELAY, slowDelay); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); slowDelay = bundle.getInt(DELAY); }
}

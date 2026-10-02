/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.wands.Wand;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Legacy SPS weakening magic: -3 effective strength and one charge drained from every wand. */
public class STRDown extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(STRDown.class)
			.t("name", "虚弱")
			.t("desc", "你感觉自己的装备突然变得沉重起来。虚弱魔法使你的有效力量降低3点。\n\n剩余时间：%s回合。")
			.t("heromsg", "你感到力量正在被抽走！");
	}




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

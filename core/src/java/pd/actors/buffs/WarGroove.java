/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** The next successful physical attack deals 50% more damage. */
public class WarGroove extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(WarGroove.class)
			.t("name", "战意律动")
			.t("desc", "下一次成功的物理攻击造成50%%额外伤害。");
	}



	{
		type = buffType.POSITIVE;
		announced = true;
	}
	@Override public int icon() { return BuffIndicator.WEAPON; }
	@Override public String desc() { return Messages.get(this, "desc"); }
}

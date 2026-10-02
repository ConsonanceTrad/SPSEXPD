/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Grants gold for successful melee attacks, as in SPS-PD 0.9.8. */
public class GoldTouch extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GoldTouch.class)
			.t("name", "点金")
			.t("desc", "臂章里的金币覆盖了你的手臂，使你在近战攻击时获得金币。\n\n剩余效果时长：%s回合。");
	}


	public static final float DURATION = 30f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.WEAPON;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}

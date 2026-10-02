/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Prevents the hero from making ordinary attacks. */
public class Disarm extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Disarm.class)
			.t("name", "缴械")
			.t("desc", "无形的麻绳锁住了你的双手，使你无法进行普通攻击。\n\n剩余效果时长：%s回合。")
			.t("cant_attack", "缴械状态下无法攻击。");
	}




	public static final float DURATION = 5f;

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.CRIPPLE;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}

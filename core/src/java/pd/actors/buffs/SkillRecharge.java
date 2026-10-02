/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Temporarily bypasses the class-skill cooldown. */
public class SkillRecharge extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SkillRecharge.class)
			.t("name", "技能充能")
			.t("desc", "技能冷却期间仍可使用职业技能。\n\n剩余效果时长：%s回合。");
	}




	public static final float DURATION = 40f;

	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.RECHARGING;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}

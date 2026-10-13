/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/**
 * SPSEXPD: 装填状态——装填期间挂在英雄身上，buff 图标上会显示剩余回合数，让装填过程更显眼。
 */
public class Reloading extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Reloading.class)
			.t("name", "装填中")
			.t("desc", "正在装填武器弹药。\n\n剩余效果时长：%s回合。");
	}

	{ type = buffType.NEUTRAL; announced = false; }

	@Override public int icon() { return BuffIndicator.RECHARGING; }

	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}

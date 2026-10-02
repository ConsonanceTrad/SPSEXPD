/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Increases damage from SPS and Shattered magic sources by 50%. */
public class MagicWeak extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MagicWeak.class)
			.t("name", "魔法易伤")
			.t("desc", "受到的魔法与状态伤害提高50%%。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.VULNERABLE; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}

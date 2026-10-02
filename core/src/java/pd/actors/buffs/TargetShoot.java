/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Legacy aimed-shot state. Missile damage is multiplied in Hero.damageRoll(). */
public class TargetShoot extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TargetShoot.class)
			.t("name", "瞄准射击")
			.t("desc", "投射武器伤害提高50%%。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.MARK; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}

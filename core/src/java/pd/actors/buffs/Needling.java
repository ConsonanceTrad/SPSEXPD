/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.actors.Char;
import pd.messages.Messages;
import pd.ui.BuffIndicator;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Needling extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Needling.class)
			.t("name", "针刺")
			.t("desc", "成功攻击会使目标护甲降低50%%或严重流血。\n\n剩余效果时长：%s回合。");
	}



	{ type = buffType.POSITIVE; announced = true; }
	public void proc(Char enemy) {
		if (Random.Int(2) == 0) Buff.prolong(enemy, ArmorBreak.class, 5f).level(50);
		else Buff.affect(enemy, Bleeding.class).set(10f);
	}
	@Override public int icon() { return BuffIndicator.THORNS; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}

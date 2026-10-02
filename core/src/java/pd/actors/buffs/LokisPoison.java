/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.InlineText;

/** The relic flail's stronger poison, which deals damage from half its remaining duration. */
public class LokisPoison extends Poison {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(LokisPoison.class)
			.t("name", "猛毒")
			.t("desc", "强烈的毒素传遍全身，并随剩余时间造成伤害。\n\n剩余中毒时长：%s回合")
			.t("heromsg", "你中毒了！");
	}



	@Override public void set(float duration) {
		left = duration;
		if (target != null) target.needsIncomingDOTUpdate = true;
	}
	@Override public boolean act() {
		if (target.isAlive()) {
			target.damage((int)(left / 2f) + 1, this);
			spend(TICK);
			if ((left -= TICK) <= 0) detach();
			else target.needsIncomingDOTUpdate = true;
		} else {
			detach();
		}
		return true;
	}
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Triples the hero's next successful attack damage roll. */
public class MoonFury extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(MoonFury.class)
			.t("name", "强力")
			.t("desc", "奇妙的力量充斥着你的身体，使你下一次成功攻击造成三倍伤害。");
	}


	{
		type = buffType.POSITIVE;
		announced = true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public void detach() {
		FullMoonStrength strength = target == null ? null : target.buff(FullMoonStrength.class);
		if (strength != null) {
			strength.detach();
		} else {
			super.detach();
		}
	}
}

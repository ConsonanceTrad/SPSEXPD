/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Superstar rhythm: +20% speed and damage, and 10% incoming damage reduction. */
public class Rhythm2 extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Rhythm2.class)
			.t("name", "超级律动")
			.t("desc", "你的速度与伤害提高20%%，受到的伤害降低10%%。\n\n剩余：%s回合。");
	}



	public static final float DURATION = 10f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.COMBO; }
}

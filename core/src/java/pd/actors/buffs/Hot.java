/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Hot extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Hot.class)
			.t("name", "灼热")
			.t("desc", "灼热状态会使受到的所有伤害提高20%%。\n\n剩余回合：%s。");
	}

	public static final float DURATION = 10f;
	{ type = buffType.NEGATIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.FIRE; }
}

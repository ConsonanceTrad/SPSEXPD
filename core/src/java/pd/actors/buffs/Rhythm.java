/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.ui.BuffIndicator;
import pd.messages.InlineText;

public class Rhythm extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Rhythm.class)
			.t("name", "节奏")
			.t("desc", "节奏使命中提高到三倍，并使闪避提高50%%。\n\n剩余回合：%s。");
	}

	public static final float DURATION = 10f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.COMBO; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Temporary complete damage immunity granted by the Mushroom Kingdom box. */
public class BoxStar extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(BoxStar.class)
			.t("name", "无敌星")
			.t("desc", "接下来%s回合内免疫所有伤害。");
	}

	public static final float DURATION = 30f;
	{ type = buffType.POSITIVE; announced = true; }
	@Override public int icon() { return BuffIndicator.IMMUNITY; }
	@Override public String desc() { return Messages.get(this, "desc", dispTurns()); }
}

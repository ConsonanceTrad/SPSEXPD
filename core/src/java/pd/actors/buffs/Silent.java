/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.buffs;

import pd.messages.Messages;
import pd.ui.BuffIndicator;
import pd.messages.InlineText;

/** Original SPS silence marker. */
public class Silent extends FlavourBuff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Silent.class)
			.t("name", "沉默")
			.t("desc", "沉默使人安静，并且让他无法诵读卷轴或释放咒语。持续%s回合。");
	}

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.LOCKED_FLOOR;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}
}

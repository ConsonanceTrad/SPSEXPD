/*
 * 层感知 —— 进入新楼层时知晓隐藏房间的数量。
 */

package pd.actors.hero.perks;

import pd.messages.InlineText;

public class LevelPerception extends Perk {

	static {
		InlineText.of(LevelPerception.class)
				.t("title", "层感知")
				.t("desc", "每当你到达新的一层，都会感知到该层隐藏房间的存在。");
	}

	public LevelPerception() {
		super(1);
	}

	@Override
	public int image() {
		return PerkImageSheet.LEVEL_PERCEPTION;
	}
}

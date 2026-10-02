/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.quest.ChallengeJournal;
import pd.messages.InlineText;

/** REN's physical challenge book, obtained in Dolya town. */
public class ChallengeBook extends ChallengeJournal {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChallengeBook.class)
			.t("name", "挑战日志")
			.t("desc", "一本普通的记事本，来自异世界的魔法使它可以记录特殊地点并开启传送。把挑战纸片夹进日志后，就能自由前往对应地点并返回。\n\n日志上粘着一张REN留下的纸条：完成所有挑战并取得隐藏奖励后，去多利亚小镇找他。\n\n已记录：_%1$d/%2$d_");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		stackable = true;
	}
}

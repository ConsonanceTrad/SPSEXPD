/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.artifacts;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 经卷耳环 —— 苦修者开局携带的占位神器（占用神器槽）。
 * 暂时和诺姆林王冠一样不提供任何效果（passiveBuff 保持基类默认的 null）。
 */
public class ScrollEarring extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ScrollEarring.class)
			.t("name", "经卷耳环")
			.t("desc", "一枚挂着微型经卷的耳环。据说它记着某位苦修者的戒律，但目前还听不出任何回响——它暂时只是一件占位神器，没有任何效果。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}
}

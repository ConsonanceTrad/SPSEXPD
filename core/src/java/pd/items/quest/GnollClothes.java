package pd.items.quest;

import pd.atlas.items.SpecificTaskDict;

import pd.items.Item;
import pd.messages.InlineText;

public class GnollClothes extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GnollClothes.class)
			.t("name", "豺狼风衣")
			.t("desc", "一件短小精致的风衣，看起来不像是人类应该有的尺寸。");
	}



	{
		image = SpecificTaskDict.GNOLL_CLOTHES;
		stackable = true;
		unique = true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
}

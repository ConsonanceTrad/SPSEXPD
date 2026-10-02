/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.SpecificTaskDict;

import pd.items.quest.AdventureJournal;
import pd.messages.Messages;
import pd.messages.InlineText;

/** Otiluke's Dolya slate, the physical journal sold after the sewer chapter. */
public class DolyaSlate extends AdventureJournal {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(DolyaSlate.class)
			.t("name", "多利亚石板")
			.t("desc", "利用多利亚盛产的魔法矿石制成的传送道具，用于不同地点之间的往返。它可以读取其他旅行者记录的地点，让你前往那些地方。")
			.t("charge", "多利亚石板当前充能数%1$d，充能上限为%2$d。")
			.t("not_enough_charge", "多利亚石板至少需要%d点充能才能打开传送通道。");
	}




	{
		image = SpecificTaskDict.DOLYA_SLATE;
		stackable = true;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc") + "\n\n"
				+ Messages.get(this, "charge", charge(), FULL_CHARGE);
	}

	@Override
	public int value() {
		return 300 * quantity;
	}
}

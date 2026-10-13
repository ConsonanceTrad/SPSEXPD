/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.ConsumUsefulUsefulDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 通用弹匣——枪械的通用弹药。一次装填消耗一个弹匣，无论枪械弹匣容量与单次击发数，
 * 都会把枪械填满。可在商店购买，或用「原石 + 暗金矿」在炼金釜合成。
 */
public class Magazine extends Item {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Magazine.class)
			.t("name", "通用弹匣")
			.t("desc", "装有标准弹药的弹匣，适用于各式枪械。装填一次即可填满枪械的弹匣，不论其容量大小。");
	}

	{
		image = ConsumUsefulUsefulDict.SP_AMMO;
		stackable = true;
		bones = false;
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 100 * quantity; }
}

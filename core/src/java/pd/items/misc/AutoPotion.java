/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.equipment.rings.Ring;
import pd.messages.InlineText;

/** Original SPS auto-potion; its legacy AutoHealPotion buff contains no active logic. */
public class AutoPotion extends Ring {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AutoPotion.class)
			.t("name", "自动药剂")
			.t("desc", "一瓶可以装备的奇特药剂。原版中自动治疗的附魔并没有实际生效。");
	}

	public AutoPotion() {
		anonymize();
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	protected RingBuff buff() {
		return new AutoHealPotion();
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public int value() {
		return 500 * quantity;
	}

	public class AutoHealPotion extends RingBuff {
	}
}

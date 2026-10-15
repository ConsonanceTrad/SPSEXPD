/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 六字真言 —— 信徒二档（王冠 B 路线）的道具。
 *
 * <p>与封魔帖同源的吸收/转移附魔，但对_非人形生物_的伤害加成_大得多_。</p>
 */
public class SixWordMantra extends SealingTalisman {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T2_2_SIXWORD;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SixWordMantra.class)
			.t("name", "六字真言")
			.t("ac_absorb", "吸收附魔")
			.t("ac_apply", "写附魔")
			.t("prompt_absorb", "选择要吸取附魔的武器")
			.t("prompt_apply", "选择要写上附魔的武器")
			.t("already_sealed", "真言里已经封着一道附魔了。")
			.t("empty", "真言里是空的，没有可写的附魔。")
			.t("no_weapon", "这不是一件武器。")
			.t("no_enchant", "这件武器上没有任何附魔。")
			.t("absorbed", "六字真言把%s吸了进去。")
			.t("applied", "你把真言里的%s写到了%s上。")
			.t("desc", "六道笔画盘成一圈，念起来像有什么在应答。\n\n「吸收附魔」：从一件武器上把附魔_吸进真言里_（该武器随之清空）。\n\n「写附魔」：把真言里封着的附魔_写到另一件武器上_（真言随之清空）。\n\n带着它时，你对_非人形生物_的攻击会狠得多（+%1$d）。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		unique = true;
	}

	@Override
	public int bonusVsNonHumanoid() {
		return 8;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}

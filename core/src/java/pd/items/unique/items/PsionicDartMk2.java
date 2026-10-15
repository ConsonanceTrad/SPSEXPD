/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.EquipmentEquipWeaponUniqueWeaponDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 灵能袖箭·改 —— 女猎手二档（王冠 A 路线）的武器。
 *
 * <p>与灵能袖箭同源，但可以把_整个弹匣_都提前预装填好（每发仍只花 0.5 回合）。</p>
 */
public class PsionicDartMk2 extends PsionicDart {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.HUNTRESS_BOW_T2_1_DART;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(PsionicDartMk2.class)
			.t("name", "灵能袖箭·改")
			.t("ac_preload", "预装填")
			.t("preloaded", "你把一次射击提前压进了袖箭（可预装填 %d 发）。")
			.t("preload_full", "袖箭已经预装填满了。")
			.t("preload_used", "预装填的一发打了出去，没有耗费回合。")
			.t("desc", "袖箭的改良型，弹仓加深了许多。\n\n「预装填」：花 _0.5 回合_把一次射击提前压进袖箭里，之后那一发打出去_不消耗回合_。它可以把_整个弹匣_都预装填好（最多 _%1$d 发_）。");
	}

	{
		//SPSEXPD: 暂借用灵能弓图标
		image = EquipmentEquipWeaponUniqueWeaponDict.SPIRIT_BOW_0;
	}

	/** SPSEXPD: 预装填全部弹匣。 */
	@Override
	protected int maxPreload() {
		return MAGAZINE;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 二代斯安威斯坦 —— 苦修者二档（王冠 A 路线）的神器。
 *
 * <p>与斯安威斯坦同源，但_充能上限加倍_，一次能换到更长的子弹时间。</p>
 */
public class SandevistanMk2 extends Sandevistan {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.ASCETIC_EARRING_T2_1_SANDEVISTAN_MK2;

	/** 充能上限（加倍）。 */
	public static final int CHARGE_CAP = Sandevistan.CHARGE_CAP * 2;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(SandevistanMk2.class)
			.t("name", "二代斯安威斯坦")
			.t("ac_activate", "开启")
			.t("no_charge", "斯安威斯坦还没充上电。")
			.t("activated", "世界被整个按了下去——只有你的手指还在跳。")
			.t("desc", "改良过的脊椎义体，接驳口加粗了一圈。\n\n「开启」：把_全部充能_一次性放掉，每 1 点充能换来 _%1$d 回合_的_子弹时间_。\n\n它的_充能上限是初代的两倍_（%2$d），一次能换来长得多的时间。\n\n子弹时间里_只有你在动_，你的攻击_照常即时结算_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.ARTIFACT_HOLDER_0;
	}

	@Override
	protected int chargeCapValue() {
		return CHARGE_CAP;
	}

	@Override
	protected float turnsPerCharge() {
		return 8f;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}

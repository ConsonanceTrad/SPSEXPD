/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.unique.items;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.items.unique.UniqueItemId;
import pd.messages.InlineText;

/**
 * SPSEXPD: 零素刺剑 —— 决斗二档（王冠 A 路线）的武器。
 *
 * <p>与蓝钢刺剑同源，但只需击破_六个方位_就能攒满「破绽百出」。</p>
 */
public class NullRapier extends BlueSteelRapier {

	public static final UniqueItemId UNIQUE_ID = UniqueItemId.DUELIST_SHIELD_T2_1_NULL;

	/** 需要击破的方位数。 */
	public static final int DIRECTIONS = 6;

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(NullRapier.class)
			.t("name", "零素刺剑")
			.t("finished", "零素刺剑没入%s的破绽，它当场毙命。")
			.t("boss_hit", "你找到了%s的破绽，这一剑造成了 %d 点伤害。")
			.t("desc", "一柄轻得像没有实体的刺剑，挥动时空气都不怎么动。\n\n它只要从敌人的 _六个方位_（相邻格或其延伸方向）各击中一次，就能把破绽全部挑开——攒满的那一击_当场斩杀_。\n\n对 _BOSS_ 而言，斩杀只会造成它_最大生命的 15%_，但破绽会重新出现，可以反复触发。\n\n在敌人周围_八格范围内_攻击它时，你会_冲刺到它的对向格_。");
	}

	{
		//SPSEXPD: 暂用占位图标
		image = SpecificPlaceHolderDict.SOMETHING_0;
		tier = 3;
		unique = true;
	}

	@Override public int min(int lvl) { return 4 + lvl; }
	@Override public int max(int lvl) { return 10 + 3 * lvl; }

	@Override
	protected int requiredDirections() {
		return DIRECTIONS;
	}

	@Override
	public UniqueItemId uniqueId() {
		return UNIQUE_ID;
	}
}

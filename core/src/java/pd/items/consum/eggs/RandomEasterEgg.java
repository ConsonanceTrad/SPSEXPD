/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.eggs;

import pd.items.consum.eggs.randomone.RandomPetEgg;
import pd.messages.InlineText;

/**
 * SPSXPD: 原「随机复活节之魂」。灵魂必须固定，所以它改为**奖励包**：
 * 使用后掉落三种复活节魂石之一。
 */
public class RandomEasterEgg extends RandomPetEgg {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(RandomEasterEgg.class)
			.t("name", "随机复活节之魂")
			.t("desc", "获得三种复活节魂石之一。")
			.t("ac_use", "使用");
	}

	public RandomEasterEgg() { super(EasterEgg.class, CocoCatEgg.class, VelociroosterEgg.class); }
}
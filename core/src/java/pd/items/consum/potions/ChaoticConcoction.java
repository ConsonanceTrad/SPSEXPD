/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions;

import java.util.Arrays;
import java.util.List;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 紊乱魔药——药水酿造失败的产物（精制 100%、粗制 75%、合剂 70%）。
 * 使用时从**粗制药水效果池**随机抽一条，好坏都有可能。提炼 3 瓶可分离出良性/恶性。
 */
public class ChaoticConcoction extends AbstractConcoction {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(ChaoticConcoction.class)
			.t("name", "紊乱魔药")
			.t("desc", "配方失败的产物：瓶里的液体翻涌不定，谁也说不准喝下去会怎样。"
					+ "用 3 瓶可以分离成 1 瓶良性紊乱魔药与 1 瓶恶性紊乱魔药。");
	}

	{
		//贴图暂用深褐药水帧占位（独立帧待美术，裁决 #9）
		image = ConsumPotionSeedBasicPotionDict.POTION_BISTRE_0;
	}

	@Override
	protected List<Class<? extends Potion>> pool() {
		return allEffects();
	}
}

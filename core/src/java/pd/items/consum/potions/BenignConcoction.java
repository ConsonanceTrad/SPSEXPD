/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions;

import java.util.Arrays;
import java.util.List;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 良性紊乱魔药——由 3 瓶紊乱魔药分离而来（docs/alchemy-rework.md §7.4）。
 * 使用时从**正面**粗制药水效果池随机抽一条。
 */
public class BenignConcoction extends AbstractConcoction {

	/** 正面粗制效果池（裁决 #6：只取粗制药水级别的好处）。 */
	public static final List<Class<? extends Potion>> BENEFICIAL = Arrays.asList(
			PotionOfHealing.class,
			PotionOfMindVision.class,
			PotionOfPurity.class,
			PotionOfHaste.class,
			PotionOfLevitation.class,
			PotionOfInvisibility.class,
			PotionOfShield.class,
			PotionOfMixing.class,
			PotionOfSatiety.class,
			PotionOfExperience.class,
			PotionOfEnergy.class,
			PotionOfStrength.class
	);

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(BenignConcoction.class)
			.t("name", "良性紊乱魔药")
			.t("desc", "分离掉坏成分后剩下的那半瓶，翻涌的液体看上去温和多了。喝下去多半是好事。");
	}

	{
		//贴图暂用品红药水帧占位（独立帧待美术，裁决 #9）
		image = ConsumPotionSeedBasicPotionDict.POTION_INVIS_0;
	}

	@Override
	protected List<Class<? extends Potion>> pool() {
		return BENEFICIAL;
	}
}

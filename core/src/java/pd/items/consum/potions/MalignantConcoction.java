/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions;

import java.util.Arrays;
import java.util.List;

import pd.atlas.items.ConsumPotionSeedBasicPotionDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 恶性紊乱魔药——由 3 瓶紊乱魔药分离而来（docs/alchemy-rework.md §7.4）。
 * 使用时从**有害**粗制药水效果池随机抽一条。
 */
public class MalignantConcoction extends AbstractConcoction {

	/** 有害粗制效果池（裁决 #6：只取粗制药水级别的坏处）。 */
	public static final List<Class<? extends Potion>> HARMFUL = Arrays.asList(
			PotionOfToxicGas.class,
			PotionOfLiquidFlame.class,
			PotionOfFrost.class,
			PotionOfParalyticGas.class,
			PotionOfConfusion.class,
			PotionOfAcid.class,
			PotionOfTransmute.class,
			PotionOfGlass.class
	);

	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(MalignantConcoction.class)
			.t("name", "恶性紊乱魔药")
			.t("desc", "分离出来的坏成分全在这半瓶里，液体黏稠发黑，靠近就让人不舒服。喝下去多半没好事。");
	}

	{
		//贴图暂用煤黑药水帧占位（独立帧待美术，裁决 #9）
		image = ConsumPotionSeedBasicPotionDict.POTION_CHARCOAL_0;
	}

	@Override
	protected List<Class<? extends Potion>> pool() {
		return HARMFUL;
	}
}

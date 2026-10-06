/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.potions.wish;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;
import pd.items.consum.potions.elixirs.Elixir;

import java.util.Arrays;
import java.util.List;

/**
 * SPSEXPD: 只有许愿才能获得的彩蛋物品。
 *
 * 这些物品不进入任何生成池与图鉴，只能通过许愿得到。
 */
public abstract class WishOnlyItem extends Elixir {

	/** 彩蛋物品固定为最高奖励等级。 */
	public static final int TIER = 4;

	private static final List<Class<? extends Item>> TYPES = Arrays.asList(
			WishFragment.class, WishCoin.class, WishStar.class);

	{
		//SPSEXPD: 贴图待指认，暂用秘药占位图
		image = SpecificPlaceHolderDict.ELIXIR_HOLDER_0;
		unique = true;
		talentFactor = 2f;
	}

	/** 全部彩蛋物品类型。 */
	public static List<Class<? extends Item>> types() {
		return TYPES;
	}
}

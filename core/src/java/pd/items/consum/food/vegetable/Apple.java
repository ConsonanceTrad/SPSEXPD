/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;

/**
 * SPSEXPD: 踩踏高草的收获——苹果。
 *
 * <p>纯食材（{@link FoodVegetable}），本身没有任何魔法效果；但它可以直接榨汁，
 * 见 {@code SpsBrewRecipes} 里的单蔬菜汁配方。
 */
public class Apple extends FoodVegetable {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(Apple.class)
			.t("name", "苹果")
			.t("desc", "踩踏高草时碰上的一颗野苹果，脆甜多汁，能填肚子，也能直接榨汁。");
	}

	{ image = SpecificPlaceHolderDict.FOOD_HOLDER_0; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

/**
 * SPSEXPD: 食材蔬菜——纯粹用来填肚子的蔬菜（胡萝卜、莴苣、松露）。
 *
 * <p>与其它蔬菜不同：它们不参与炼金——{@link pd.items.Recipe#usableInRecipe} 会直接拒绝，
 * 也不会作为炼金材料出现在炼金界面的候选里。
 */
public abstract class FoodVegetable extends Vegetable {
	{
		image = SpecificPlaceHolderDict.FOOD_HOLDER_0;
	}
}

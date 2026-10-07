package pd.items.consum.food.processed;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.items.Item;

/**
 * SPSEXPD: 蔬菜的二次加工产物——把收获到的蔬菜放进炼金釜再加工得到。
 * 本身不能被直接使用（不能食用、也不能作为丰裕之角的食物），只作为进一步合成的材料。
 */
public abstract class Processed extends Item {
	{
		image = SpecificPlaceHolderDict.FOOD_HOLDER_0;
		//SPSEXPD: 不再是食物，但仍作为炼金/烹饪原料，保持可堆叠
		stackable = true;
	}

	//SPSEXPD: 二次作物（二次加工产物）不再继承 Food —— 没有饱食度、没有食用动作，
	//也不再被丰裕之角 / 帐篷 / 宠物喂养当作食物识别。
	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 6 * quantity; }
}

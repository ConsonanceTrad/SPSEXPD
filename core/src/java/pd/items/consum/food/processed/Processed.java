package pd.items.consum.food.processed;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Hunger;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;

import java.util.ArrayList;

/**
 * SPSEXPD: 蔬菜的二次加工产物——把收获到的蔬菜放进炼金釜再加工得到。
 * 本身不能被直接使用（不能食用、也不能作为丰裕之角的食物），只作为进一步合成的材料。
 */
public abstract class Processed extends Food {
	{
		image = SpecificPlaceHolderDict.FOOD_HOLDER_0;
		energy = Hunger.HUNGRY / 12f;
		hornValue = 0;
		bones = false;
	}
	//SPSEXPD: 二次加工产物只能用于合成，不再可以直接食用
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(Food.AC_EAT);
		return actions;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		onEat(hero);
	}
	protected void onEat(Hero hero) {
	}
	@Override protected float eatingTime() { return 1f; }
	@Override public int value() { return 6 * quantity; }
}

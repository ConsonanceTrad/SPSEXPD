/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.sprites.ItemSprite;

public class IceMeat extends MeatFood {
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0044FF);
	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { IceMeat result = new IceMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Invisibility.class, 20f);
	}
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 3 * quantity; }
}

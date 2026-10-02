/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.sprites.ItemSprite;
import pd.messages.InlineText;

public class IceMeat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(IceMeat.class)
			.t("name", "冻肉片")
			.t("desc", "被魔法冻住的生肉薄片。提供的能量不多，却带有一种有用的祝福。");
	}



	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0044FF);
	{
		image = ConsumFoodFoodDict.FROZEN_MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { IceMeat result = new IceMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Invisibility.class, 20f);
	}
	@Override public ItemSprite.Glowing glowing() { return BLUE; }
	@Override public int value() { return 3 * quantity; }
}

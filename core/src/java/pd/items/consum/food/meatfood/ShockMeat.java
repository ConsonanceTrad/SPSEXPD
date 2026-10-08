/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Drowsy;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.items.consum.food.Food;
import pd.messages.InlineText;

public class ShockMeat extends MeatFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ShockMeat.class)
			.t("name", "炸里脊")
			.t("desc", "经过强电流加工的肉，香酥可口，食用后令人神清气爽。");
	}



	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 100f;
	}
	public static Food cook(int quantity) { ShockMeat result = new ShockMeat(); result.quantity(quantity); return result; }
	@Override protected void doEat(Hero hero) {
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
		Buff.detach(hero, Drowsy.class);
		Buff.detach(hero, Slow.class);
		Buff.detach(hero, Vertigo.class);
	}
	@Override public int value() { return 3 * quantity; }
}

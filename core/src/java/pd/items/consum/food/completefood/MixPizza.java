/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Light;
import pd.actors.hero.Hero;
import pd.items.Item;
import render.utils.math.Random;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class MixPizza extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MixPizza.class)
			.t("name", "混合披萨")
			.t("desc", "这玩意如果让部落的某些人看到，你就要小心他们了。\n使用_1份主食、1份原石、1份水果、1份肉、1份蔬菜_锻造。");
	}




	{
		image = ConsumFoodFoodDict.PIZZA;
		energy = 50f;
	}

	public MixPizza() { this(4); }
	public MixPizza(int number) { quantity = number; }

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Bless.class, 10f);
		Buff.affect(hero, Light.class, 10f);
		Buff.affect(hero, HasteBuff.class, 10f);
		Buff.affect(hero, Levitation.class, 10f);
	}

	@Override public Item random() { quantity = Random.Int(3, 6); return this; }
	@Override public int value() { return 10 * quantity; }
}

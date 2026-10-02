/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Notice;
import pd.actors.hero.Hero;
import pd.items.Item;
import render.utils.math.Random;
import pd.messages.InlineText;

public class FruitCandy extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(FruitCandy.class)
			.t("name", "水果硬糖")
			.t("desc", "制作糖丸需要好多步骤，但是在这里只要这么简单就能做了。\n使用_1份水、1份水果、1份原石_锻造。");
	}


	{
		image = ConsumFoodFoodDict.FRUIT_CANDY;
		energy = 20f;
	}

	public FruitCandy() { this(2); }
	public FruitCandy(int number) { quantity = number; }

	@Override
	protected void doEat(Hero hero) {
		switch (Random.Int(3)) {
			case 0:
				Buff.affect(hero, HasteBuff.class, 20f);
				Buff.affect(hero, Levitation.class, 20f);
				break;
			case 1:
				Buff.affect(hero, Notice.class, Notice.DURATION);
				break;
			default:
				Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 10, 10));
				break;
		}
	}

	@Override public Item random() { quantity = Random.Int(2, 4); return this; }
	@Override public int value() { return 10 * quantity; }
}

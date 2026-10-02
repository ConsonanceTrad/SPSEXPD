/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Levitation;
import pd.actors.buffs.Notice;
import pd.actors.hero.Hero;
import pd.items.Item;
import render.utils.math.Random;

public class FruitCandy extends CompleteFood {

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

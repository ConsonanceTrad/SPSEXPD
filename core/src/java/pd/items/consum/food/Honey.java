/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.Item;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Honey extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Honey.class)
			.t("name", "蜂蜜")
			.t("desc", "浓稠甘甜的蜂蜜，食用后能永久增强生命力。");
	}

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 50f;
		hornValue = 0;
	}
	public Honey() { this(1); }
	public Honey(int number) { quantity = number; }
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		hero.HTBoost += Random.Int(2, 4);
		hero.updateHT(true);
	}
	@Override public Item random() { quantity = Random.Int(1, 2); return this; }
	@Override public int value() { return 500 * quantity; }
}

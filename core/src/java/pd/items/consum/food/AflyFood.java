/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AflyBless;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

/** The original Fushigi-no rice ball made by Alfred. */
public class AflyFood extends Food {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(AflyFood.class)
			.t("name", "不思议饭团")
			.t("desc", "阿飞特制的不思议饭团，拥有另一个世界的力量。\n让阿飞制作。");
	}



	{
		image = ConsumFoodFoodDict.AFLY_FOOD;
		energy = 200f;
	}
	@Override protected void satisfy(Hero hero) {
		super.satisfy(hero);
		Buff.affect(hero, AflyBless.class, 150f);
	}
	@Override public int value() { return 2 * quantity; }
}

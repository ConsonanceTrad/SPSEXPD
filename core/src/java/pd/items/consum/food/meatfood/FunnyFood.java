/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.FunnyBuff;
import pd.actors.hero.Hero;

public class FunnyFood extends MeatFood {
	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 500f;
	}
	@Override protected void doEat(Hero hero) {
		Buff.prolong(hero, FunnyBuff.class, 1600f);
	}
	@Override public int value() { return quantity; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.items.Item;
import render.utils.math.Random;

public class Honey extends Food {
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

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.sprites.ItemSpriteSheet;
import render.utils.Random;

public class Honey extends Food {
	{
		image = ItemSpriteSheet.SPS_HONEY;
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

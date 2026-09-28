/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

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

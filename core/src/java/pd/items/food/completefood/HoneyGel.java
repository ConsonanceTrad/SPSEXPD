/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

public class HoneyGel extends CompleteFood {
	private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF44);
	{ image = ItemSpriteSheet.HONEY_GEL; energy = 20f; }
	@Override protected void doEat(Hero hero) { increaseMaxHealth(hero, 3, 6); }
	@Override public ItemSprite.Glowing glowing() { return YELLOW; }
	@Override public int value() { return 400 * quantity; }
}

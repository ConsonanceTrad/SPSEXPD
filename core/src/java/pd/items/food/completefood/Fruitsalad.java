/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Fruitsalad extends CompleteFood {
	{ image = ItemSpriteSheet.FRUIT_SALAD; energy = 130f; }
	@Override protected void doEat(Hero hero) {
		heal(hero, hero.HT / 3);
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 2, 30));
	}
	@Override public int value() { return 2 * quantity; }
}

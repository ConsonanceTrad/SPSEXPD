/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Honeymeat extends CompleteFood {
	{ image = ItemSpriteSheet.HONEY_MEAT; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}
	@Override public int value() { return 400 * quantity; }
}

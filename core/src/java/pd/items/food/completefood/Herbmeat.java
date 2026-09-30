/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Herbmeat extends CompleteFood {
	{ image = ItemSpriteSheet.HERB_MEAT; energy = 180f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 70f).level(30); }
	@Override public int value() { return 2 * quantity; }
}

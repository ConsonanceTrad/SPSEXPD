/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Chocolate extends CompleteFood {
	{ image = ItemSpriteSheet.CHOCOLATE; energy = 300f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, ShieldArmor.class).level(hero.HT); }
	@Override public int value() { return 60 * quantity; }
}

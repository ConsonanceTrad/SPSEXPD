/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Mediummeat extends CompleteFood {
	{
		image = ItemSpriteSheet.STEAK;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 50f).level(60); }
	@Override public int value() { return 3 * quantity; }
}

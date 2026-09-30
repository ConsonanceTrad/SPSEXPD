/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class Icecream extends CompleteFood {
	{ image = ItemSpriteSheet.ICECREAM; energy = 90f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		heal(hero, (hero.HT - hero.HP) / 2);
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Burning.class);
		Buff.detach(hero, STRDown.class);
	}
	@Override public int value() { return 300 * quantity; }
}

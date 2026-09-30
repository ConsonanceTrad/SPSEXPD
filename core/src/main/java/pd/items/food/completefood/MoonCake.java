/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

public class MoonCake extends CompleteFood {

	{
		image = ItemSpriteSheet.MOON_CAKE;
		energy = 360f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 3);
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 3);
	}

	@Override public int value() { return 3 * quantity; }
}

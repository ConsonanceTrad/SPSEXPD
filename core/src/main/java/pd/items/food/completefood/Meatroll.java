/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

public class Meatroll extends CompleteFood {

	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);

	{
		image = ItemSpriteSheet.HOTDOG;
		energy = 250f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public int value() { return 3 * quantity; }
}

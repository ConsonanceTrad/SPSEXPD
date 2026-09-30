/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;
import pd.sprites.ItemSpriteSheet;

public class Vegetablekebab extends CompleteFood {

	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);

	{
		image = ItemSpriteSheet.KEBAB;
		energy = 150f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 2);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public int value() { return 2 * quantity; }
}

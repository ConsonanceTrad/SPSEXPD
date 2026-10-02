/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;

public class Kebab extends CompleteFood {
	private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0xCC6600);
	{ image = ConsumFoodFoodDict.KEBAB; energy = 330f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, AttackUp.class, 50f).level(40);
	}
	@Override public ItemSprite.Glowing glowing() { return BROWN; }
	@Override public int value() { return 5 * quantity; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.sprites.ItemSprite;

public class Vegetableroll extends CompleteFood {
	private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x22CC44);
	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 170f; }
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
	}
	@Override public ItemSprite.Glowing glowing() { return GREEN; }
	@Override public int value() { return 6 * quantity; }
}

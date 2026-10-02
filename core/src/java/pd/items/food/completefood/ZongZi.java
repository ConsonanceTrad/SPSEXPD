/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.Slow;
import pd.actors.buffs.Tar;
import pd.actors.hero.Hero;

public class ZongZi extends CompleteFood {
	{
		image = ConsumFoodFoodDict.ZONGZI;
		energy = 600f;
		stackable = false;
	}
	@Override protected void doEat(Hero hero) {
		Buff.affect(hero, Tar.class);
		Buff.affect(hero, Slow.class, 30f);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 4);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}
	@Override public int value() { return 60 * quantity; }
}

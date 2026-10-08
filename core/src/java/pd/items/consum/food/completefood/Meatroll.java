/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Recharging;
import pd.actors.buffs.SuperArcane;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class Meatroll extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Meatroll.class)
			.t("name", "肉卷")
			.t("desc", "这意味着可以吃书了。\n使用_1份卷轴、1份肉_炼制。");
	}





	{
		image = ConsumFoodFoodDict.CHICKEN_WRAP;
		energy = 250f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Recharging.class, 20f);
		Buff.affect(hero, SuperArcane.class, 40f).level(5);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public int value() { return 3 * quantity; }
}

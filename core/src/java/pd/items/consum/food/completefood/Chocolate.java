/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Chocolate extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Chocolate.class)
			.t("name", "巧克力")
			.t("desc", "超纯的巧克力，超苦的。\n使用_5份坚果_炼金。");
	}



	{ image = ConsumFoodFoodDict.CHOCOLATE; energy = 300f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, ShieldArmor.class).level(hero.HT); }
	@Override public int value() { return 60 * quantity; }
}

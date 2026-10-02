/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Sishimi extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Sishimi.class)
			.t("name", "生鱼片")
			.t("desc", "新鲜的鱼片，食用后会获得魔法护盾。");
	}



	{
		image = ConsumFoodFoodDict.MEAT;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, MagicArmor.class).level(hero.HT / 5); }
	@Override public int value() { return 3 * quantity; }
}

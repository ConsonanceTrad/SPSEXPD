/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class HoneyWater extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HoneyWater.class)
			.t("name", "蜂糖水")
			.t("desc", "稀释后的蜂蜜。\n使用_2份水、1份蜂蜜_炼金。");
	}



	{ image = ConsumFoodFoodDict.DILUTED_HONEY; energy = 10f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
	}
	@Override public int value() { return 200 * quantity; }
}

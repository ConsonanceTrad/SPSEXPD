/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Honeyrice extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Honeyrice.class)
			.t("name", "蜂蜜拌饭")
			.t("desc", "天哪，我嘴里塞满了蜜蜂！\n使用_1份主食、1份蜂蜜_炼金。");
	}



	{ image = ConsumFoodFoodDict.HONEY_RICE; energy = 500f; }
	@Override protected void doEat(Hero hero) { increaseMaxHealth(hero, 3, 6); }
	@Override public int value() { return 400 * quantity; }
}

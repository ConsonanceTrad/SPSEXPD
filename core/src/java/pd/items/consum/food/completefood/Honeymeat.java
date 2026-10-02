/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.ConsumFoodFoodDict;

public class Honeymeat extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Honeymeat.class)
			.t("name", "蜜汁肉排")
			.t("desc", "把蜂蜜浇在肉排上……很甜。\n使用_1份蜂蜜、1份肉_炼金。");
	}



	{ image = ConsumFoodFoodDict.HONEY_ROAST_MEAT; energy = 150f; }
	@Override protected void doEat(Hero hero) {
		increaseMaxHealth(hero, 3, 6);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}
	@Override public int value() { return 400 * quantity; }
}

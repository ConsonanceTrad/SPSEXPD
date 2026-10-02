/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Chickennugget extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Chickennugget.class)
			.t("name", "椒盐鸡块")
			.t("desc", "想吃我的香香鸡吗？\n使用_1份原石、1份肉_炼金。");
	}



	{ image = ConsumFoodFoodDict.CHICKENNUGGET; energy = 170f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 50f).level(20); }
	@Override public int value() { return 2 * quantity; }
}

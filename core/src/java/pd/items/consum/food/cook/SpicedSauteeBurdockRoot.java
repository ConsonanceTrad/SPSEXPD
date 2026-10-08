/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.items.consum.food.completefood.CompleteFood;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——香草炒牛蒡（烹饪技巧 50+ 的产物）。 */
public class SpicedSauteeBurdockRoot extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SpicedSauteeBurdockRoot.class)
			.t("name", "香草炒牛蒡")
			.t("desc", "加了鼠尾草与迷迭香同炒的牛蒡根，闻起来很香。食用后短时间提升攻击。");
	}

	{ image = ConsumFoodFoodDict.KEBAB; energy = 250f; }

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public int value() { return 15 * quantity; }
}

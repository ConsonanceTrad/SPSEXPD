/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.items.consum.food.completefood.CompleteFood;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——香草烤菊苣（烹饪技巧 95+ 的顶级产物）。 */
public class BakedChicoryWithHerbs extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BakedChicoryWithHerbs.class)
			.t("name", "香草烤菊苣")
			.t("desc", "菊苣块茎配阳春草与碎血莓烤制的大餐，闻起来非常香。食用后恢复生命并提升攻击。");
	}

	{ image = ConsumFoodFoodDict.HEARTY_MEAL; energy = 500f; }

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(Math.max(1, hero.HT / 10), 0, 5);
		Buff.affect(hero, AttackUp.class, 50f).level(30);
	}

	@Override public int value() { return 60 * quantity; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;
import pd.items.consum.food.completefood.CompleteFood;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——香草烤小胡萝卜（烹饪技巧 50+ 的产物）。 */
public class TenderRoastedBabyCarrots extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(TenderRoastedBabyCarrots.class)
			.t("name", "香草烤小胡萝卜")
			.t("desc", "用香料调味的烤小胡萝卜，看起来很美味。食用后还会缓缓恢复少量生命。");
	}

	{ image = ConsumFoodFoodDict.NUT_COOKIE; energy = 300f; }

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(Math.max(1, hero.HT / 20), 0, 5);
	}

	@Override public int value() { return 15 * quantity; }
}

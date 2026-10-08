/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.cook;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.items.consum.food.completefood.CompleteFood;
import pd.messages.InlineText;

/** SPSEXPD: 血源风烹饪产物——慢烤鼠（烹饪技巧 25+ 的产物）。 */
public class SlowRoastedRat extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SlowRoastedRat.class)
			.t("name", "慢烤鼠")
			.t("desc", "用香料一同烤制的老鼠。意外地不错，还能压住体内的毒性。");
	}

	{ image = ConsumFoodFoodDict.ROAST_MEAT; energy = 200f; }

	@Override
	protected void doEat(Hero hero) {
		Buff.detach(hero, Poison.class);
	}

	@Override public int value() { return 15 * quantity; }
}

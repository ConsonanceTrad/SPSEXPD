/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.meatfood;

import pd.actors.buffs.Buff;
import pd.actors.buffs.Poison;
import pd.actors.hero.Hero;
import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import render.utils.math.Random;

/**
 * SPSEXPD: 从鱼身上割下的生鱼。
 * 鱼（食人鱼系）不再掉生肉，改掉这个；可生食，也可交给煎锅烹饪。
 */
public class RawFish extends MeatFood {
	//SPSEXPD: inline Chinese text
	static {
		InlineText.of(RawFish.class)
			.t("name", "生鱼")
			.t("desc", "刚从鱼身上割下来的生鱼，鳞光犹在。最好加工后再吃。");
	}

	{
		image = SpecificPlaceHolderDict.FOOD_HOLDER_0;
		energy = 100f;
		//SPSEXPD: 可以交给煎锅烹饪（兜底产出烤制品）
		canBeCook = true;
	}

	@Override protected void doEat(Hero hero) {
		//SPSEXPD: 与生肉一样，有小概率吃坏肚子
		if (Random.Int(15) == 0) Buff.affect(hero, Poison.class).set(hero.HT / 5f);
	}

	@Override public int value() { return 2 * quantity; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.buffs.ShieldArmor;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class MoonCake extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(MoonCake.class)
			.t("name", "月饼")
			.t("desc", "中秋佳节赏月，是壁垒众多传统之一。\n使用_1份主食、2份坚果_炼制。");
	}




	{
		image = ConsumFoodFoodDict.MOON_CAKE;
		energy = 360f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 3);
		Buff.affect(hero, ShieldArmor.class).level(hero.HT / 3);
	}

	@Override public int value() { return 3 * quantity; }
}

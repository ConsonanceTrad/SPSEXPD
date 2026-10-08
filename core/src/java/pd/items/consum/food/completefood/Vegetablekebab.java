/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Vegetablekebab extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Vegetablekebab.class)
			.t("name", "大菜串")
			.t("desc", "素食主义者最喜欢的。\n使用_2份蔬菜、1份肉_炼制。");
	}





	{
		image = ConsumFoodFoodDict.KEBAB;
		energy = 150f;
	}

	@Override
	protected void doEat(Hero hero) {
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 2);
		Buff.affect(hero, AttackUp.class, 50f).level(20);
	}

	@Override public int value() { return 2 * quantity; }
}

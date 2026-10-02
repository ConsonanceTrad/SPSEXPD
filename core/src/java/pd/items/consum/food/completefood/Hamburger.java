/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.ConsumFoodFoodDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.MagicArmor;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Hamburger extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Hamburger.class)
			.t("name", "巨无霸汉堡")
			.t("desc", "啊！好大！\n使用_2份主食、1份蔬菜、2份肉_炼金。");
	}



	{ image = ConsumFoodFoodDict.HAMBURGER; energy = 770f; }
	@Override protected void doEat(Hero hero) {
		heal(hero, hero.HT / 5);
		Buff.affect(hero, MagicArmor.class).level(hero.HT / 3);
		Buff.affect(hero, AttackUp.class, 50f).level(70);
	}
	@Override public int value() { return 10 * quantity; }
}

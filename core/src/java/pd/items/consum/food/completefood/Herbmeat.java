/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Herbmeat extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Herbmeat.class)
			.t("name", "药草酱肉")
			.t("desc", "沾草后口味更佳。\n使用_1份种子、1份肉_炼金。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; energy = 180f; }
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 70f).level(30); }
	@Override public int value() { return 2 * quantity; }
}

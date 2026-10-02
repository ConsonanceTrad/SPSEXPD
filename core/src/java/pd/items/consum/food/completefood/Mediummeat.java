/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.consum.food.completefood;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class Mediummeat extends CompleteFood {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Mediummeat.class)
			.t("name", "七分熟肉排")
			.t("desc", "精心烹制的肉排，能在一段时间内大幅提高攻击力。");
	}



	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		energy = 180f;
	}
	@Override protected void doEat(Hero hero) { Buff.affect(hero, AttackUp.class, 50f).level(60); }
	@Override public int value() { return 3 * quantity; }
}

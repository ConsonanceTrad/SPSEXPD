package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;
import pd.messages.InlineText;

public class BattleFlower extends Vegetable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BattleFlower.class)
			.t("name", "星花瓣")
			.t("desc", "星陨花的一部分，可以食用。它能强化物理攻击，并提供祝福与奥术防护。");
	}

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Bless.class, 30f);
		Buff.affect(hero, ArcaneArmor.class).set(3 + hero.lvl / 4, 30);
		Buff.affect(hero, PhysicalEmpower.class).set(3 + hero.lvl / 3, 5);
	}
}

package pd.items.consum.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Bless;
import pd.actors.buffs.Buff;
import pd.actors.buffs.PhysicalEmpower;
import pd.actors.hero.Hero;

public class BattleFlower extends Vegetable {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onEat(Hero hero) {
		Buff.prolong(hero, Bless.class, 30f);
		Buff.affect(hero, ArcaneArmor.class).set(3 + hero.lvl / 4, 30);
		Buff.affect(hero, PhysicalEmpower.class).set(3 + hero.lvl / 3, 5);
	}
}

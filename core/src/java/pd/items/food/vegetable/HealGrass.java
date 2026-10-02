package pd.items.food.vegetable;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Healing;
import pd.actors.hero.Hero;

public class HealGrass extends Vegetable {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onEat(Hero hero) {
		Buff.affect(hero, Healing.class).setHeal(20, 0.25f, 0);
		Buff.affect(hero, ArcaneArmor.class).set(Math.max(1, hero.HT / 5), 20);
	}
}

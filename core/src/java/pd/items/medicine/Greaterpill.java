package pd.items.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Poison;
import pd.actors.buffs.STRDown;
import pd.actors.hero.Hero;

public class Greaterpill extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, BerryRegeneration.class).level(Math.max(hero.HT / 2, 30));
		hero.HP += Math.min(hero.HT, hero.HT * 2 - hero.HP);
		Buff.detach(hero, Poison.class);
		Buff.detach(hero, Cripple.class);
		Buff.detach(hero, STRDown.class);
		Buff.detach(hero, Bleeding.class);
	}
	@Override public int value() { return 50 * quantity; }
}

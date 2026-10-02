package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.Buff;
import pd.actors.buffs.GrowSeed;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;

public class GoldenJelly extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public GoldenJelly() { this(1); }
	public GoldenJelly(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) Buff.affect(mob, GrowSeed.class).set(10f);
		Buff.affect(hero, Vertigo.class, 10f);
	}
}

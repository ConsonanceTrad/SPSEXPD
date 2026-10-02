package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.BeCorrupt;
import pd.actors.buffs.BeOld;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;

public class DeathCap extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public DeathCap() { this(1); }
	public DeathCap(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, BeOld.class).set(50f);
			Buff.affect(mob, BeCorrupt.class).level(50);
		}
		hero.damage(Math.max(1, hero.HP / 2), this);
		Buff.prolong(hero, Cripple.class, Cripple.DURATION);
	}
}

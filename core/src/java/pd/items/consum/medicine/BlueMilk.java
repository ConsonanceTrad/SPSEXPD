package pd.items.consum.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.buffs.AttackDown;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.Slow;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;

public class BlueMilk extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	public BlueMilk() { this(1); }
	public BlueMilk(int value) { quantity = value; }
	@Override protected void onUse(Hero hero) {
		for (Mob mob : mobs()) {
			Buff.affect(mob, Slow.class, 50f);
			Buff.affect(mob, AttackDown.class, 50f).level(50);
		}
		Buff.affect(hero, HasteBuff.class, 10f);
		Buff.affect(hero, BerryRegeneration.class).level(hero.HP / 2);
	}
}

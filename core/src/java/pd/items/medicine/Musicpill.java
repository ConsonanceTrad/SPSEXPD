package pd.items.medicine;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Rhythm2;
import pd.actors.buffs.Rhythm;
import pd.actors.buffs.WarGroove;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroClass;
import pd.actors.hero.HeroSubClass;
import pd.effects.Speck;

public class Musicpill extends Pill {
	{ image = SpecificPlaceHolderDict.SOMETHING_0; }
	@Override protected void onUse(Hero hero) {
		Buff.affect(hero, Rhythm.class, 800f);
		if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.PERFORMER) {
			Buff.affect(hero, WarGroove.class);
		}
		if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.SUPERSTAR) {
			Buff.affect(hero, Rhythm2.class, 800f);
		}
		if (hero.sprite != null) hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.4f, 4);
	}
	@Override public int value() { return 50 * quantity; }
}

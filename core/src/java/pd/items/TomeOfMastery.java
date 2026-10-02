/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items;

import pd.atlas.items.ConsumUsefulProcessEnhanceDict;

import pd.actors.buffs.Blindness;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.messages.Messages;
import pd.utils.GLog;

import java.util.ArrayList;

/** The SPS leather tome which offers the reader's two original subclasses. */
public class TomeOfMastery extends TengusMask {
	public static final String AC_READ = "READ";
	public static final float TIME_TO_READ = 10f;
	private Hero reader;
	{
		image = ConsumUsefulProcessEnhanceDict.MASTERY_0;
		defaultAction = AC_READ;
	}
	@Override public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove("WEAR");
		if (!actions.contains(AC_READ)) actions.add(AC_READ);
		return actions;
	}
	@Override public void execute(Hero hero, String action) {
		if (!AC_READ.equals(action)) {
			super.execute(hero, action);
			return;
		}
		if (hero.buff(Blindness.class) != null) {
			GLog.w(Messages.get(this, "blind"));
			return;
		}
		reader = hero;
		super.execute(hero, "WEAR");
	}
	@Override public void choose(HeroSubClass way) {
		if (reader != null) reader.spend(TIME_TO_READ - 1f);
		super.choose(way);
	}
}

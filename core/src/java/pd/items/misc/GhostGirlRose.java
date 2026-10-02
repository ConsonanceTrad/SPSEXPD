/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;

/** WhiteGhost's charm, granting two additional experience points per gain event. */
public class GhostGirlRose extends MiscEquippable {

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }

	@Override protected GhostGirlBless createBuff() { return new GhostGirlBless(); }

	public static int experienceBonus(Hero hero) {
		return hero.buff(GhostGirlBless.class) == null ? 0 : 2;
	}

	public class GhostGirlBless extends MiscBuff { }

	@Override public int value() { return 500 * quantity; }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.actors.hero.Hero;
import pd.sprites.ItemSpriteSheet;

/** WhiteGhost's charm, granting two additional experience points per gain event. */
public class GhostGirlRose extends MiscEquippable {

	{ image = ItemSpriteSheet.GHOST_GIRL_ROSE; }

	@Override protected GhostGirlBless createBuff() { return new GhostGirlBless(); }

	public static int experienceBonus(Hero hero) {
		return hero.buff(GhostGirlBless.class) == null ? 0 : 2;
	}

	public class GhostGirlBless extends MiscBuff { }

	@Override public int value() { return 500 * quantity; }
}

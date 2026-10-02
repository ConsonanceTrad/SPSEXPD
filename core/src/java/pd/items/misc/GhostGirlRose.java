/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.hero.Hero;
import pd.messages.InlineText;
import pd.atlas.items.GroundFunctionalFallingDict;

/** WhiteGhost's charm, granting two additional experience points per gain event. */
public class GhostGirlRose extends MiscEquippable {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GhostGirlRose.class)
			.t("name", "幽魂的余香")
			.t("desc", "幽魂存在的证明之一，可以提升经验的获取。");
	}




	{ image = GroundFunctionalFallingDict.PETAL_0; }

	@Override protected GhostGirlBless createBuff() { return new GhostGirlBless(); }

	public static int experienceBonus(Hero hero) {
		return hero.buff(GhostGirlBless.class) == null ? 0 : 2;
	}

	public class GhostGirlBless extends MiscBuff { }

	@Override public int value() { return 500 * quantity; }
}

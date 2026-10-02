/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.misc;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.actors.Char;
import pd.actors.hero.Hero;

/** AliveFish's charm: swift movement in water and protection from fisher creatures. */
public class FishBone extends MiscEquippable {

	{ image = SpecificPlaceHolderDict.SOMETHING_0; }

	@Override protected FishFriend createBuff() { return new FishFriend(); }

	public static float waterSpeedMultiplier(Hero hero, boolean inWater) {
		return inWater && hero.buff(FishFriend.class) != null ? 2f : 1f;
	}

	public static boolean protectsFrom(Hero hero, Object source) {
		return hero.buff(FishFriend.class) != null && source instanceof Char
				&& Char.hasProp((Char)source, Char.Property.FISHER);
	}

	public class FishFriend extends MiscBuff { }

	@Override public int value() { return 500 * quantity; }
}

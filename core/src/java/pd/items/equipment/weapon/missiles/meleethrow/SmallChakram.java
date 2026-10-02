/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;


public class SmallChakram extends MeleeThrowWeapon {
	{
		image = ConsumThrowsDict.ROUND_BLADE;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SmallChakram.class)
			.t("name", "小圆刃")
			.t("desc", "只要能熟练使用，这种小型圆刃既能近战，也能投掷并回收。——Consideredhamster");
	}



	public SmallChakram() { super(2, 11, 23, SpecificPlaceHolderDict.SOMETHING_0); }
}

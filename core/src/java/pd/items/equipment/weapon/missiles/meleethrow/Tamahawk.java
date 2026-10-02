/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.meleethrow;

import pd.atlas.items.SpecificPlaceHolderDict;
import pd.messages.InlineText;
import pd.atlas.items.ConsumThrowsDict;


public class Tamahawk extends MeleeThrowWeapon {
	{
		image = ConsumThrowsDict.TOMAHAWK_0;
	}
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Tamahawk.class)
			.t("name", "飞斧")
			.t("desc", "这种沉重的投掷斧也可以装备用于近战。——Watabou");
	}



	public Tamahawk() { super(5, 53, 68, SpecificPlaceHolderDict.SOMETHING_0); }
}

/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.bags;

import pd.items.Item;
import pd.items.ShadowEaterKey;
import pd.items.armor.Armor;
import pd.items.weapon.melee.MeleeWeapon;
import pd.sprites.ItemSpriteSheet;

/** The original portable training target, used as a thirty-slot equipment bag. */
public class HeartOfScarecrow extends Bag {

	{
		image = ItemSpriteSheet.HEART_OF_SCARECROW;
	}

	@Override
	public boolean canHold(Item item) {
		if (item instanceof MeleeWeapon || item instanceof Armor || item instanceof ShadowEaterKey) {
			return super.canHold(item);
		}
		return false;
	}

	@Override public int capacity() { return 34; }
	@Override public int value() { return 50 * quantity; }
}
